"use client"

import { useEffect } from "react"

const CompareModal = ({ laptops, onClose }) => {
  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === "Escape") {
        onClose()
      }
    }

    document.addEventListener("keydown", handleEscape)
    return () => document.removeEventListener("keydown", handleEscape)
  }, [onClose])

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose()
    }
  }

  const specs = ["brand", "processor", "memory", "storage", "graphics", "display", "price"]

  return (
    <div className="modal-backdrop" onClick={handleBackdropClick}>
      <div className="compare-modal-content">
        <button className="modal-close" onClick={onClose}>
          ×
        </button>

        <h2>Compare Laptops</h2>

        <div className="compare-table">
          <div className="compare-header">
            <div className="spec-label"></div>
            {laptops.map((laptop) => (
              <div key={laptop.id} className="laptop-header">
                <img src={laptop.image || "/placeholder.svg"} alt={laptop.name} />
                <h3>{laptop.name}</h3>
              </div>
            ))}
          </div>

          {specs.map((spec) => (
            <div key={spec} className="compare-row">
              <div className="spec-label">{spec.charAt(0).toUpperCase() + spec.slice(1)}:</div>
              {laptops.map((laptop) => (
                <div key={laptop.id} className="spec-value">
                  {laptop[spec]}
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
