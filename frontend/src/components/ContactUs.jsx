"use client"
import { useState } from "react"
import "./ContactUs.css"

const ContactUs = () => {
  const [formData, setFormData] = useState({ name: "", email: "", phone: "" })
  const [message, setMessage] = useState("")
  const [loading, setLoading] = useState(false)

  // Team members data
  const teamMembers = [
    {
      id: 1,
      name: "John Smith",
      photo: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
    },
    {
      id: 2,
      name: "Sarah Johnson",
      photo: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face"
    },
    {
      id: 3,
      name: "Mike Chen",
      photo: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face"
    },
    {
      id: 4,
      name: "Emma Davis",
      photo: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face"
    },
    {
      id: 5,
      name: "Alex Rodriguez",
      photo: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face"
    }
  ]

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage("")

    try {
      const response = await fetch("http://localhost:8080/validateContact", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      })

      const data = await response.json()
      setMessage(data.message)

      // Reset form on success
      if (response.ok) {
        setFormData({ name: "", email: "", phone: "" })
      }
    } catch (error) {
      console.error("Contact form submission failed:", error)
      setMessage("Failed to submit contact form. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="contact-form-container">
      <h2>Contact Us</h2>
      
      {/* Team Members Section */}
      <div className="team-section">
        <h3>Our Team</h3>
        <div className="team-members">
          {teamMembers.map((member) => (
            <div key={member.id} className="team-member">
              <div className="member-photo">
                <img 
                  src={member.photo} 
                  alt={member.name}
                  onError={(e) => {
                    e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(member.name)}&background=7e4ee5&color=fff&size=150`
                  }}
                />
              </div>
              <p className="member-name">{member.name}</p>
            </div>
          ))}
        </div>
      </div>

      <form onSubmit={handleSubmit} className="contact-form">
        <input
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="Name"
          required
          disabled={loading}
        />
        <input
          name="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Email"
          type="email"
          required
          disabled={loading}
        />
        <input
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          placeholder="Phone"
          required
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          {loading ? "Submitting..." : "Submit"}
        </button>
      </form>
      
      {message && <p className="contact-response">{message}</p>}
    </div>
  )
}

export default ContactUs