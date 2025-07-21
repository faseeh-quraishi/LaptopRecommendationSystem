// laptopService.js

// Simulate API delay helper
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

// Function to fetch laptops data from your real API, now accepts query
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
    const response = await fetch("http://localhost:8080/WebApi", settings);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    // Assuming data is already in the correct format
    return data;
  } catch (error) {
    console.error("Failed to fetch laptops from API:", error);
    // Return empty array or fallback mock data if needed
    return [];
  }
};

// Main search function that fetches from API and applies filtering, sorting, pagination
export const searchLaptops = async (query, filters, sortBy, page = 1, limit = 12) => {
  await delay(500); // Optional simulated delay for realism

  console.log("🔍 Fetching laptops from API with query:", query);

  // Get all laptops from real API with query
  const allLaptops = await fetchLaptopsFromApi(query);

  let results = [...allLaptops];

  // Filter by search query (in name, brand, or processor)
//   if (query) {
// results = results.filter(laptop =>
//   laptop.name?.toLowerCase().includes(query.toLowerCase()) ||
//   laptop.brand?.toLowerCase().includes(query.toLowerCase()) ||
//   laptop.processor?.toLowerCase().includes(query.toLowerCase())
// );
//   }

  // Brand filter
  if (filters.brands && filters.brands.length > 0) {
    results = results.filter((laptop) => filters.brands.includes(laptop.brand));
  }

  // RAM filter
  if (filters.ram && filters.ram.length > 0) {
    results = results.filter((laptop) => filters.ram.includes(laptop.memory));
  }

  // Storage filter
  if (filters.storage && filters.storage.length > 0) {
    results = results.filter((laptop) => filters.storage.includes(laptop.storage));
  }

  // Graphics filter
  if (filters.graphics && filters.graphics.length > 0) {
    results = results.filter((laptop) => filters.graphics.some((g) => laptop.graphics.includes(g)));
  }

  // Price filter
  if (filters.priceRange && filters.priceRange.length === 2) {
    results = results.filter((laptop) => {
      const price = Number.parseInt(laptop.price.replace("$", "").replace(",", ""));
      return price >= filters.priceRange[0] && price <= filters.priceRange[1];
    });
  }

  // Sorting
  if (sortBy === "price-low-high") {
    results.sort((a, b) => {
      const priceA = Number.parseInt(a.price.replace("$", "").replace(",", ""));
      const priceB = Number.parseInt(b.price.replace("$", "").replace(",", ""));
      return priceA - priceB;
    });
  } else if (sortBy === "price-high-low") {
    results.sort((a, b) => {
      const priceA = Number.parseInt(a.price.replace("$", "").replace(",", ""));
      const priceB = Number.parseInt(b.price.replace("$", "").replace(",", ""));
      return priceB - priceA;
    });
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

// Autocomplete suggestions (kept mock for simplicity)
export const getAutocompleteSuggestions = async (query) => {
  await delay(200);
  const suggestions = [
    "Dell Inspiron",
    "HP Pavilion",
    "Lenovo ThinkPad",
    "Asus VivoBook",
    "MacBook Air",
    "MSI Gaming",
    "Acer Aspire",
    "gaming laptop",
    "business laptop",
    "student laptop",
    "Intel i7",
    "AMD Ryzen",
    "NVIDIA RTX",
    "ultrabook",
    "2-in-1 laptop",
  ];
  return suggestions
    .filter((s) => s.toLowerCase().includes(query.toLowerCase()))
    .slice(0, 5);
};

// Mock analytics data (unchanged)
const mockSearchFrequency = {
  gaming: 145,
  business: 89,
  student: 67,
  programming: 54,
  design: 43,
  ultrabook: 38,
  "2-in-1": 29,
  workstation: 21,
};

const mockWordFrequency = {
  laptop: 456,
  intel: 234,
  amd: 189,
  ssd: 334,
  gaming: 145,
  rtx: 98,
  fhd: 267,
  touchscreen: 76,
};

export const getSearchFrequency = async (word) => {
  const response = await fetch("http://localhost:8080/WebApi", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ method: "increaseSearchFrequencyCount", word }),
  });

  if (!response.ok) throw new Error("Failed to fetch search frequency");
  const data = await response.json();
  return data.result; // Adjust depending on your backend response structure
};

export const getWordFrequency = async () => {
  
  const response = await fetch("http://localhost:8080/WebApi", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ method: "getTop5SearchedWords" }),
  });

  if (!response.ok) throw new Error("Failed to fetch word frequency");
  const data = await response.json();

  const freqMap = {};
  if (Array.isArray(data.result)) {
    data.result.forEach(({ word, count }) => {
      freqMap[word] = Number(count);
    });
  }
  return freqMap;
};