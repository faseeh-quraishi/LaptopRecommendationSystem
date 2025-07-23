"use client"
import { useState } from "react"
import "./ContactUs.css"
import Hasnain_Mirza from '../teamphotos/Hasnain_Mirza.jpeg'
import Faseeh from '../teamphotos/Faseeh.jpg'
import Jill from '../teamphotos/Jill.jpeg'
import Kamraan from '../teamphotos/Kamraan.jpeg'
import Rohan from '../teamphotos/Rohan.jpeg'

const nameRegex = /^[A-Za-z ]{2,25}$/;
const emailRegex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,6}$/i;
const phoneRegex = /^(\(\d{3}\)|\d{3})[\s-]?\d{3}[\s-]?\d{4}$/;

const ContactUs = () => {
  const [formData, setFormData] = useState({ name: "", email: "", phone: "" })
  const [message, setMessage] = useState("")
  const [messageType, setMessageType] = useState("") // 'error' or 'success'
  const [loading, setLoading] = useState(false)

  // Team members data
  const teamMembers = [
    { id: 1, name: "Hasnain Mirza", photo: Hasnain_Mirza, linkedin: "https://www.linkedin.com/in/hasnain-mirza79/" },
    { id: 2, name: "Faseeh Qureshi", photo: Faseeh, linkedin: "https://www.linkedin.com/in/faseeh-u-rehman/" },
    { id: 3, name: "Jill Patel", photo: Jill, linkedin: "https://www.linkedin.com/in/jill-patel-7hb/" },
    { id: 4, name: "Kamraan Ahmed", photo: Kamraan, linkedin: "https://www.linkedin.com/in/kamraan-ahmed-09b1962b6/" },
    { id: 5, name: "Rohan", photo: Rohan, linkedin: "https://www.linkedin.com/in/rohan-here/" }
  ]

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const validate = () => {
    let errors = []
    if (!nameRegex.test(formData.name.trim())) {
      errors.push("Error in name: Only letters and spaces, 2-25 characters.")
    }
    if (!emailRegex.test(formData.email.trim())) {
      errors.push("Error in email: Invalid email format.")
    }
    if (!phoneRegex.test(formData.phone.trim())) {
      errors.push("Error in phone: (Canadian format).")
    }
    return errors
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMessage("")
    setMessageType("")
    setLoading(true)
    const errors = validate()
    if (errors.length > 0) {
      setMessage("Invalid details. " + errors.join(" "))
      setMessageType("error")
      setLoading(false)
      return
    }
    try {
      // Simulate API call or use your real API here
      await new Promise((resolve) => setTimeout(resolve, 800))
      setMessage("Sent successfully!")
      setMessageType("success")
      setFormData({ name: "", email: "", phone: "" })
    } catch (error) {
      setMessage("Failed to submit contact form. Please try again.")
      setMessageType("error")
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
              <div className="member-photo" style={{cursor: 'pointer'}} onClick={() => window.open(member.linkedin, '_blank', 'noopener,noreferrer')}>
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
          type="text"
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
      {message && (
        <p className={`contact-response${messageType === 'success' ? ' contact-success' : ''}`}>
          {message}
        </p>
      )}
    </div>
  )
}

export default ContactUs