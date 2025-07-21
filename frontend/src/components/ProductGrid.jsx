"use client"

import ProductCard from "./ProductCard"

const ProductGrid = ({
  laptops,
  loading,
  onLaptopClick,
  onCompareToggle,
  compareList,
  pagination,
  searchQuery,
}) => {
  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Searching for laptops...</p>
      </div>
    )
  }

  if (laptops.length === 0) {
    return (
      <div className="no-results">
        <p>No laptops found matching your criteria.</p>
      </div>
    )
  }

  return (
    <div className="product-grid-container">
      {pagination && (
        <div className="results-info">
          <p>
            Showing {(pagination.currentPage - 1) * pagination.itemsPerPage + 1} -{" "}
            {Math.min(pagination.currentPage * pagination.itemsPerPage, pagination.totalItems)} of{" "}
            {pagination.totalItems} laptops
          </p>
        </div>
      )}

      <div className="product-grid">
        {laptops.map((laptop) => {
          const isSelected = compareList.some((item) => item.id === laptop.id)
          const canSelect = isSelected || compareList.length < 3

          return (
            <ProductCard
              key={laptop.id}
              laptop={laptop}
              onClick={() => onLaptopClick(laptop)}
              onCompareToggle={() => onCompareToggle(laptop)}
              isSelected={isSelected}
              canSelect={canSelect}
              searchQuery={searchQuery}
            />
          )
        })}
      </div>
    </div>
  )
}

export default ProductGrid
