"use client"

import { useState, useEffect } from "react"
import SearchBar from "./components/SearchBar"
import FilterBar from "./components/FilterBar"
import SortDropdown from "./components/SortDropdown"
import ProductGrid from "./components/ProductGrid"
import ProductModal from "./components/ProductModal"
import CompareBar from "./components/CompareBar"
import CompareModal from "./components/CompareModal"
import Pagination from "./components/Pagination"
import { searchLaptops, getSearchFrequency, getWordFrequency } from "./api/laptopService"
import "./App.css"

function App() {
  const [laptops, setLaptops] = useState([])
  const [filteredLaptops, setFilteredLaptops] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchQuery, setSearchQuery] = useState("")
  const [filters, setFilters] = useState({
    brands: [],
    ram: [],
    storage: [],
    display: [],
    graphics: [],
    priceRange: [0, 3000],
  })
  const [sortBy, setSortBy] = useState("")
  const [selectedLaptop, setSelectedLaptop] = useState(null)
  const [compareList, setCompareList] = useState([])
  const [showCompareModal, setShowCompareModal] = useState(false)
  const [searchFrequency, setSearchFrequency] = useState({})
  const [wordFrequency, setWordFrequency] = useState({})
  const [hasSearched, setHasSearched] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)
  const [pagination, setPagination] = useState({
    currentPage: 1,
    totalPages: 1,
    totalItems: 0,
    itemsPerPage: 12,
    hasNextPage: false,
    hasPrevPage: false,
  })

  useEffect(() => {
    if (
      searchQuery ||
      Object.values(filters).some((f) => (Array.isArray(f) ? f.length > 0 : f !== filters.priceRange))
    ) {
      setCurrentPage(1) // Reset to first page when filters change
      handleSearch(1)
    }
  }, [searchQuery, filters, sortBy])

  const handleSearch = async (page = 1) => {
    setLoading(true)
    setHasSearched(true)

    try {
      // API call with pagination parameters
      const response = await searchLaptops(searchQuery, filters, sortBy, page, 12)

      // Assign unique id using index for each laptop to fix compare issue
      const laptopsWithIds = response.laptops.map((laptop, index) => ({
        ...laptop,
        id: index,
      }))

      setLaptops(laptopsWithIds)
      setFilteredLaptops(laptopsWithIds)
      setPagination(response.pagination)
      setCurrentPage(page)

      // Fetch frequency data (only on first page to avoid unnecessary calls)
      if (page === 1) {
        const searchFreq = await getSearchFrequency()
        setSearchFrequency(searchFreq)

        if (searchQuery.trim()) {
          try {
            const wordFreqRaw = await getWordFrequency(searchQuery.trim())
            setWordFrequency(wordFreqRaw)
          } catch (err) {
            console.warn("Could not fetch word frequency:", err)
            setWordFrequency({})
          }
        } else {
          setWordFrequency({})
        }
      }
    } catch (error) {
      console.error("Search failed:", error)
    } finally {
      setLoading(false)
    }
  }

  const handleCompareToggle = (laptop) => {
    setCompareList((prev) => {
      const isSelected = prev.find((item) => item.id === laptop.id)
      if (isSelected) {
        return prev.filter((item) => item.id !== laptop.id)
      } else if (prev.length < 3) {
        return [...prev, laptop]
      }
      return prev
    })
  }

  const clearFilters = () => {
    setFilters({
      brands: [],
      ram: [],
      storage: [],
      display: [],
      graphics: [],
      priceRange: [0, 3000],
    })
    setSearchQuery("")
    setHasSearched(false)
    setCurrentPage(1)
    setLaptops([])
    setFilteredLaptops([])
    setPagination({
      currentPage: 1,
      totalPages: 1,
      totalItems: 0,
      itemsPerPage: 12,
      hasNextPage: false,
      hasPrevPage: false,
    })
  }

  const handlePageChange = (page) => {
    handleSearch(page)
    // Scroll to top when page changes
    window.scrollTo({ top: 0, behavior: "smooth" })
  }

  return (
    <div className="app">

      <div className="group-section">
        <div className="group-container">
          <h3>Group-4 AlgoAllies</h3>
          <p>Your Ultimate Laptop Search Tool</p>
        </div>
      </div>

      <header className="app-header">
        <h1>Laptop Recommendation System</h1>
      </header>

      <main className="app-main">
        <div className="search-section">
          <SearchBar
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={() => handleSearch(1)} // Called when user presses Enter
          />

          <FilterBar
            filters={filters}
            onFiltersChange={setFilters}
            onClearFilters={clearFilters}
            searchFrequency={searchFrequency}
            wordFrequency={wordFrequency}
          />

          {hasSearched && <SortDropdown value={sortBy} onChange={setSortBy} />}
        </div>

        {!hasSearched ? (
          <div className="welcome-section">
            <div className="welcome-content">
              <img src="/placeholder.svg?height=200&width=300" alt="Welcome" className="welcome-image" />
              <h2>Let's get you the best laptop you can get!</h2>
              <p>Use the search bar and filters above to find your perfect laptop</p>
            </div>
          </div>
        ) : (
          <ProductGrid
            laptops={filteredLaptops}
            loading={loading}
            onLaptopClick={setSelectedLaptop}
            onCompareToggle={handleCompareToggle}
            compareList={compareList}
            pagination={pagination}
            searchQuery={searchQuery} // ✅ Correct
          />
        )}

        {hasSearched && !loading && filteredLaptops.length > 0 && pagination.totalPages > 1 && (
          <Pagination
            currentPage={pagination.currentPage}
            totalPages={pagination.totalPages}
            onPageChange={handlePageChange}
          />
        )}
      </main>

      {selectedLaptop && <ProductModal laptop={selectedLaptop} onClose={() => setSelectedLaptop(null)} />}

      {compareList.length >= 2 && (
        <CompareBar
          compareList={compareList}
          onCompare={() => setShowCompareModal(true)}
          onRemove={handleCompareToggle}
        />
      )}

      {showCompareModal && <CompareModal laptops={compareList} onClose={() => setShowCompareModal(false)} />}
    </div>
  )
}

export default App
