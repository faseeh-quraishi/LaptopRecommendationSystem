"use client"

const CompareBar = ({ compareList, onCompare, onRemove }) => {
  return (
    <div className="compare-bar">
      <div className="compare-items">
        {compareList.map((laptop) => (
          <div key={laptop.id} className="compare-item">
            <span>{laptop.brandName} {laptop.product}</span>
            <button className="remove-compare" onClick={() => onRemove(laptop)}>
              ×
            </button>
          </div>
        ))}
      </div>

      <button className="compare-button" onClick={onCompare}>
        Compare ({compareList.length})
      </button>
    </div>
  )
}

export default CompareBar
