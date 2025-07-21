"use client"

import { useEffect } from "react"

const ProductModal = ({ laptop, onClose }) => {
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

  return (
    <div className="modal-backdrop" onClick={handleBackdropClick}>
      <div className="modal-content">
        <button className="modal-close" onClick={onClose}>×</button>

        <div className="modal-body">
          <div className="modal-image">
            <img src={laptop.image || "/placeholder.svg"} alt={laptop.product} />
            <div className="price-large">{laptop.price}</div>
          </div>

          <div className="modal-details">
            <h2>{laptop.product}</h2>
            <div className="specs-grid">
              <div className="spec-item"><strong>Brand:</strong> {laptop.brandName}</div>
              <div className="spec-item"><strong>Processor:</strong> {laptop.processor}</div>
              <div className="spec-item"><strong>Memory:</strong> {laptop.memory}</div>
              <div className="spec-item"><strong>Storage:</strong> {laptop.storage}</div>
              <div className="spec-item"><strong>Graphics:</strong> {laptop.graphics}</div>
              <div className="spec-item"><strong>Display:</strong> {laptop.display}</div>
              <div className="spec-item"><strong>OS:</strong> {laptop.os}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductModal
