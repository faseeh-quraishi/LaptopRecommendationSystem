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

import ContactUs from "./components/ContactUs"
import CrawlerPage from "./components/CrawlerPage"

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

  const [showContactModal, setShowContactModal] = useState(false)
  const [showCrawlerModal, setShowCrawlerModal] = useState(false)

  useEffect(() => {
    if (showContactModal) {
      document.body.classList.add('modal-open');
    } else {
      document.body.classList.remove('modal-open');
    }
    return () => {
      document.body.classList.remove('modal-open');
    };
  }, [showContactModal]);

  // Trigger search only when filters or sortBy change (not searchQuery)
  useEffect(() => {
    const hasActiveFilters = Object.values(filters).some((f) =>
      Array.isArray(f) ? f.length > 0 : f !== filters.priceRange
    )

    if (hasActiveFilters || sortBy) {
      setCurrentPage(1)
      handleSearch(1)
    }
  }, [filters, sortBy]) // ✅ removed searchQuery here

  const handleSearch = async (page = 1) => {
    setLoading(true)
    setHasSearched(true)

    try {
      const response = await searchLaptops(searchQuery, filters, sortBy, page, 12)

      const laptopsWithIds = response.laptops.map((laptop, index) => ({
        ...laptop,
        id: index,
      }))

      setLaptops(laptopsWithIds)
      setFilteredLaptops(laptopsWithIds)
      setPagination(response.pagination)
      setCurrentPage(page)

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
    window.scrollTo({ top: 0, behavior: "smooth" })
  }

  return (
    <div className="app">
      <div className="header-row">
        <div className="group-title">
          <h3 className="group-link" onClick={() => setShowContactModal(true)} style={{cursor: 'pointer'}}>
            Group-4 AlgoAllies
          </h3>
        </div>
        <div className="main-title">
          <h1>Laptop Recommendation System</h1>
        </div>
      </div>

      <main className="app-main">
        <div className="search-section">
          <SearchBar
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={() => handleSearch(1)} // Only triggers on Enter now
          />

          <FilterBar
            filters={filters}
            onFiltersChange={setFilters}
            onClearFilters={clearFilters}
          />

          {hasSearched && (
            <div className="sort-and-frequency-row">
              <SortDropdown value={sortBy} onChange={setSortBy} />
              {Object.keys(searchFrequency).length > 0 && (
                <div className="frequency-group">
                  <div className="frequency-display">
                    <div className="frequency-section">
                      <h4>Search Frequency:</h4>
                      <div className="frequency-items">
                        {Object.entries(searchFrequency)
                          .slice(0, 5)
                          .map(([term, count]) => (
                            <span key={term} className="frequency-item">
                              {term}: {count}
                            </span>
                          ))}
                      </div>
                    </div>
                    <div className="frequency-section">
                      <h4>Word Frequency:</h4>
                      <div className="frequency-items">
                        {Object.entries(wordFrequency)
                          .slice(0, 5)
                          .map(([word, count]) => (
                            <span key={word} className="frequency-item">
                              {word}: {count}
                            </span>
                          ))}
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
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
            searchQuery={searchQuery}
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

      {/* For regex Validation
      <ContactUs />
      <CrawlerPage /> */}

      {/* Action Buttons */}
      <div className="action-buttons">
        <button className="action-btn contact-btn" onClick={() => setShowContactModal(true)}>
          Contact Us
        </button>
        <button className="action-btn crawler-btn" onClick={() => setShowCrawlerModal(true)}>
          Web Crawler
        </button>
      </div>

      {/* Contact Us Modal */}
      {showContactModal && (
        <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && setShowContactModal(false)}>
          <div className="modal-content contact-modal">
            <button className="modal-close" onClick={() => setShowContactModal(false)}>
              ×
            </button>
            <ContactUs />
          </div>
        </div>
      )}

      {/* Web Crawler Modal */}
      {showCrawlerModal && (
        <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && setShowCrawlerModal(false)}>
          <div className="modal-content crawler-modal">
            <button className="modal-close" onClick={() => setShowCrawlerModal(false)}>
              ×
            </button>
            <CrawlerPage />
          </div>
        </div>
      )}



      {showCompareModal && <CompareModal laptops={compareList} onClose={() => setShowCompareModal(false)} />}
    </div>
  )
}

export default App