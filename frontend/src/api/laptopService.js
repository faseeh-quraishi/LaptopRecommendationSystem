const API_URL = "http://localhost:8080/WebApi";

// Utility delay for simulating real-world delay (optional)
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

// ----------------------------------------------
// 🔍 Fetch laptops using your real API
// ----------------------------------------------
export const fetchLaptopsFromApi = async (query = "") => {
  const settings = {
    method: "POST",
    headers: {
      "Content-Type": "text/plain",
    },
    body: JSON.stringify({
      spelling: `${query}`,      // Send dynamic query here
      method: "SearchProduct",
    }),
  };

  try {
    const response = await fetch(API_URL, settings);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const data = await response.json();

    console.log("📦 Laptop API response:", data); // ✅ Debug log

    // ✅ Check for ["error: message"] format
    if (Array.isArray(data) && data.length === 1 && typeof data[0] === "string" && data[0].toLowerCase().startsWith("error:")) {
      console.warn("🛑 API returned error:", data[0]);
      return [];
    }

    return data;
  } catch (error) {
    console.error("❌ Failed to fetch laptops from API:", error);
    return [];
  }
};

// ----------------------------------------------
// 🧠 Word Completion API call (Autocomplete)
// ----------------------------------------------
export const getAutocompleteSuggestions = async (prefix) => {
  const settings = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      method: "WordCompletion",
      prefix,
    }),
  };

  try {
    const response = await fetch(API_URL, settings);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const data = await response.json();
    return data.result || [];
  } catch (error) {
    console.error("❌ WordCompletion failed:", error);
    return [];
  }
};

// ----------------------------------------------
// ✅ Spell Check API call
// ----------------------------------------------
export const spellCheckQuery = async (spelling) => {
  const settings = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      method: "spellCheck",
      spelling,
    }),
  };

  try {
    const response = await fetch(API_URL, settings);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const data = await response.json();
    return data.result || []; // return full result array (suggestions or error)
  } catch (error) {
    console.error("❌ Spell check failed:", error);
    return ["error: Request failed"];
  }
};

// ----------------------------------------------
// 🔎 Main Search Logic (with filters, sorting, pagination)
// ----------------------------------------------
export const searchLaptops = async (query, filters, sortBy, page = 1, limit = 12) => {
  await delay(500); // Optional simulated delay

  console.log("🔍 Searching laptops with query:", query);

  const allLaptops = await fetchLaptopsFromApi(query);
  let results = [...allLaptops];

  // Apply filters
  if (filters.brands?.length) {
    results = results.filter((laptop) => filters.brands.includes(laptop.brand));
  }

  if (filters.ram?.length) {
    results = results.filter((laptop) => filters.ram.includes(laptop.memory));
  }

  if (filters.storage?.length) {
    results = results.filter((laptop) => filters.storage.includes(laptop.storage));
  }

  if (filters.graphics?.length) {
    results = results.filter((laptop) =>
      filters.graphics.some((g) => laptop.graphics?.toLowerCase().includes(g.toLowerCase()))
    );
  }

  if (filters.priceRange?.length === 2) {
    results = results.filter((laptop) => {
      const price = Number.parseInt(laptop.price.replace("$", "").replace(",", ""));
      return price >= filters.priceRange[0] && price <= filters.priceRange[1];
    });
  }

  // Sorting
  if (sortBy === "price-low-high") {
    results.sort((a, b) =>
      parseInt(a.price.replace("$", "").replace(",", "")) -
      parseInt(b.price.replace("$", "").replace(",", ""))
    );
  } else if (sortBy === "price-high-low") {
    results.sort((a, b) =>
      parseInt(b.price.replace("$", "").replace(",", "")) -
      parseInt(a.price.replace("$", "").replace(",", ""))
    );
  }

  // Pagination
  const totalItems = results.length;
  const totalPages = Math.ceil(totalItems / limit);
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;
  const paginatedResults = results.slice(startIndex, endIndex);

  return {
    laptops: paginatedResults,
    pagination: {
      currentPage: page,
      totalPages,
      totalItems,
      itemsPerPage: limit,
      hasNextPage: page < totalPages,
      hasPrevPage: page > 1,
    },
  };
};

// ----------------------------------------------
// 📊 Search & Word Frequency (Mock)
// ----------------------------------------------
export const getSearchFrequency = async () => {
  await delay(100);
  return {
    gaming: 145,
    business: 89,
    student: 67,
    programming: 54,
    design: 43,
    ultrabook: 38,
    "2-in-1": 29,
    workstation: 21,
  };
};

export const getWordFrequency = async () => {
  await delay(100);
  return {
    laptop: 456,
    intel: 234,
    amd: 189,
    ssd: 334,
    gaming: 145,
    rtx: 98,
    fhd: 267,
    touchscreen: 76,
  };
};
