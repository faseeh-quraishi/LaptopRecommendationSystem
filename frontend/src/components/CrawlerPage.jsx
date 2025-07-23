"use client"

import { useState } from "react"
import "./CrawlerPage.css"

const CrawlerPage = () => {
const allowedUrls = [
  {
    label: "ASUS Website",
    value: "https://www.asus.com/ca-en/support/callus/"
  },
  {
    label: "Apple Website",
    value: "https://www.apple.com/ca/contact/"
  },
  {
    label: "HP Website",
    value: "https://www.hp.com/ca-en/shop/offer.aspx?p=contact-hp-store"
  },
  {
    label: "Acer Website",
    value: "https://acer.pissedconsumer.com/customer-service.html"
  },
  {
    label: "Dell Website",
    value: "https://www.dell.com/en-ca/lp/contact-us"
  }
]

  const [url, setUrl] = useState("")
  const [crawlResults, setCrawlResults] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    const isValid = allowedUrls.some(item => item.value === url)
    if (!isValid) {
      alert("Invalid URL. Please select from the allowed list.")
      return
    }

    setLoading(true)
    setCrawlResults(null)

    try {
      const response = await fetch("http://localhost:8080/WebApi", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          method: "crawlAndExtract",
          url: url
        }),
      })

      const data = await response.json()

      if (response.ok && data && data.result) {
        setCrawlResults(data.result)
        setUrl("")
      } else {
        throw new Error("Invalid response from server.")
      }

    } catch (error) {
      console.error("Web crawling failed:", error)
      alert("Failed to crawl website. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  const splitAndFilter = (value) => {
    if (!value || typeof value !== 'string') return []
    return value.split(',').map(item => item.trim()).filter(item => item.length > 0)
  }

  return (
    <div className="crawler-container">
      <h2>Website Crawler</h2>
      <p>Contact information from official site</p>
      <form onSubmit={handleSubmit} className="crawler-form">
        <select value={url} onChange={(e) => setUrl(e.target.value)} disabled={loading}>
          <option value="">Select Website</option>
          {allowedUrls.map((item, index) => (
            <option key={index} value={item.value}>
              {item.label}
            </option>
          ))}
        </select>
        <button type="submit" disabled={!url || loading}>
          {loading ? "Crawling..." : "Crawl"}
        </button>
      </form>

      <div className="crawler-results">
        {loading && (
          <div className="crawler-loading">
            <div className="loading-spinner"></div>
            <p>Crawling website...</p>
          </div>
        )}

        {crawlResults && (
          <div className="results-container">
            <h3>Crawl Results</h3>

            {/* Phone Numbers */}
            {crawlResults.Phone && (
              <div className="result-section">
                <h4>Phone Numbers</h4>
                <ul>
                  {splitAndFilter(crawlResults.Phone).slice(0, 5).map((phone, index) => (
                    <li key={index}>{phone}</li>
                  ))}
                </ul>
                {splitAndFilter(crawlResults.Phone).length > 5 && (
                  <p className="more-results">
                    +{splitAndFilter(crawlResults.Phone).length - 5} more phone numbers found
                  </p>
                )}
              </div>
            )}

            {/* Email Addresses */}
            {crawlResults.Email && (
              <div className="result-section">
                <h4>Email Addresses</h4>
                <ul>
                  {splitAndFilter(crawlResults.Email).slice(0, 5).map((email, index) => (
                    <li key={index}>{email}</li>
                  ))}
                </ul>
                {splitAndFilter(crawlResults.Email).length > 5 && (
                  <p className="more-results">
                    +{splitAndFilter(crawlResults.Email).length - 5} more email addresses found
                  </p>
                )}
              </div>
            )}

            {/* URLs */}
            {crawlResults.URL && (
              <div className="result-section">
                <h4>URLs</h4>
                <ul>
                  {splitAndFilter(crawlResults.URL).slice(0, 5).map((url, index) => (
                    <li key={index}>
                      <a href={url} target="_blank" rel="noopener noreferrer">
                        {url}
                      </a>
                    </li>
                  ))}
                </ul>
                {splitAndFilter(crawlResults.URL).length > 5 && (
                  <p className="more-results">
                    +{splitAndFilter(crawlResults.URL).length - 5} more URLs found
                  </p>
                )}
              </div>
            )}

            {/* Handle case where no data is found */}
            {!crawlResults.Phone && !crawlResults.Email && !crawlResults.URL && (
              <p className="no-results">No contact information found on this website.</p>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default CrawlerPage
