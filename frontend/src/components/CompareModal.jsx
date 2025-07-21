"use client"

import { useEffect } from "react"

const CompareModal = ({ laptops, onClose }) => {
  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === "Escape") onClose()
    }

    document.addEventListener("keydown", handleEscape)
    return () => document.removeEventListener("keydown", handleEscape)
  }, [onClose])

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) onClose()
  }

  const specLabels = [
    "Brand",
    "Product",
    "Price",
    "Processor",
    "Memory",
    "Storage",
    "Graphics",
    "Display",
    "OS"
  ]

  const getValue = (laptop, label) => {
    switch (label) {
      case "Brand": return laptop.brandName
      case "Product": return laptop.product
      case "Price": return laptop.price
      case "Processor": return laptop.processor
      case "Memory": return laptop.memory
      case "Storage": return laptop.storage
      case "Graphics": return laptop.graphics
      case "Display": return laptop.display
      case "OS": return laptop.os
      default: return ""
    }
  }

  return (
    <div className="modal-backdrop" onClick={handleBackdropClick}>
      <div className="compare-modal-content">
        <button className="modal-close" onClick={onClose}>×</button>
        <h2>Compare Laptops</h2>

        <div className="compare-table">
          <div className="compare-header">
            <div></div>
            {laptops.map((laptop) => (
              <div key={laptop.id} className="laptop-header">
                <img src={laptop.image || "/placeholder.svg"} alt={laptop.product} />
                <h3>{laptop.product}</h3>
              </div>
            ))}
          </div>

          {specLabels.map((label) => (
            <div key={label} className="compare-row">
              <div className="spec-label">{label}</div>
              {laptops.map((laptop) => (
                <div key={laptop.id + label} className="spec-value">
                  {getValue(laptop, label)}
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default CompareModal
