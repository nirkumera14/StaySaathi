const FAVORITES_KEY = "staysaathi:favorites";
const LEGACY_FAVORITES_KEY = "pgpulse:favorites";
const existingFavorites =
  localStorage.getItem(FAVORITES_KEY) ||
  localStorage.getItem(LEGACY_FAVORITES_KEY) ||
  "[]";

const state = {
  page: 0,
  size: 9,
  total: 0,
  listings: [],
  activeListings: [],
  loading: false,
  compareIds: new Set(),
  favorites: new Set(JSON.parse(existingFavorites)),
  showFavoritesOnly: false,
  citySuggestions: [],
  activeCitySuggestionIndex: -1,
  cityRequestToken: 0,
};

const el = {
  listingGrid: document.getElementById("listingGrid"),
  resultCount: document.getElementById("resultCount"),
  resultTitle: document.getElementById("resultTitle"),
  emptyState: document.getElementById("emptyState"),
  loadMore: document.getElementById("loadMore"),
  applyFilters: document.getElementById("applyFilters"),
  clearFilters: document.getElementById("clearFilters"),
  quickSearchForm: document.getElementById("quickSearchForm"),
  quickSearchInput: document.getElementById("quickSearchInput"),
  metricListings: document.getElementById("metricListings"),
  metricAvgRent: document.getElementById("metricAvgRent"),
  compareBar: document.getElementById("compareBar"),
  compareCount: document.getElementById("compareCount"),
  compareDialog: document.getElementById("compareDialog"),
  compareContent: document.getElementById("compareContent"),
  openCompare: document.getElementById("openCompare"),
  closeCompare: document.getElementById("closeCompare"),
  favoriteCount: document.getElementById("favoriteCount"),
  favoritesBtn: document.getElementById("favoritesBtn"),
  cityInput: document.getElementById("cityInput"),
  citySuggestionList: document.getElementById("citySuggestionList"),
  activeFilterPills: document.getElementById("activeFilterPills"),
  queryInput: document.getElementById("queryInput"),
  genderInput: document.getElementById("genderInput"),
  roomTypeInput: document.getElementById("roomTypeInput"),
  minRentInput: document.getElementById("minRentInput"),
  maxRentInput: document.getElementById("maxRentInput"),
  foodInput: document.getElementById("foodInput"),
  verifiedInput: document.getElementById("verifiedInput"),
  sortInput: document.getElementById("sortInput"),
  toast: document.getElementById("toast"),
};

let toastTimer;

function debounce(callback, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => callback(...args), delay);
  };
}

function showToast(message) {
  clearTimeout(toastTimer);
  el.toast.textContent = message;
  el.toast.classList.remove("hidden");
  toastTimer = setTimeout(() => el.toast.classList.add("hidden"), 1700);
}

function readFilters() {
  const amenities = [
    ...document.querySelectorAll('input[name="amenity"]:checked'),
  ].map((node) => node.value);
  return {
    city: el.cityInput.value.trim(),
    q: el.queryInput.value.trim(),
    gender: el.genderInput.value,
    roomType: el.roomTypeInput.value,
    minRent: el.minRentInput.value,
    maxRent: el.maxRentInput.value,
    foodIncluded: el.foodInput.checked,
    verified: el.verifiedInput.checked,
    sort: el.sortInput.value,
    amenities,
  };
}

function buildQuery(filters, page) {
  const params = new URLSearchParams();
  params.set("page", String(page));
  params.set("size", String(state.size));
  params.set("sort", filters.sort || "RELEVANCE");

  if (filters.city) params.set("city", filters.city);
  if (filters.q) params.set("q", filters.q);
  if (filters.gender) params.set("gender", filters.gender);
  if (filters.roomType) params.set("roomType", filters.roomType);
  if (filters.minRent) params.set("minRent", filters.minRent);
  if (filters.maxRent) params.set("maxRent", filters.maxRent);
  if (filters.foodIncluded) params.set("foodIncluded", "true");
  if (filters.verified) params.set("verified", "true");
  filters.amenities.forEach((amenity) => params.append("amenities", amenity));

  return params.toString();
}

function renderSkeletonCards(count = 6) {
  el.listingGrid.innerHTML = Array.from({ length: count })
    .map(
      () => `
            <article class="skeleton-card">
                <div class="skeleton-image"></div>
                <div class="skeleton-content">
                    <div class="skeleton-line long"></div>
                    <div class="skeleton-line medium"></div>
                    <div class="skeleton-line short"></div>
                </div>
            </article>
        `,
    )
    .join("");
}

async function fetchListings(reset = false) {
  if (state.loading) return;
  state.loading = true;
  closeCitySuggestionList();

  const filters = readFilters();
  renderActiveFilterPills(filters);

  if (reset) {
    state.page = 0;
    state.listings = [];
    state.activeListings = [];
    if (!state.showFavoritesOnly) {
      renderSkeletonCards();
    }
  }

  const query = buildQuery(filters, state.page);

  try {
    el.resultCount.textContent = "Loading listings...";
    const response = await fetch(`/api/listings?${query}`);
    if (!response.ok) {
      throw new Error("Failed to load listings");
    }

    const data = await response.json();
    state.total = data.total;
    state.activeListings = reset
      ? data.items
      : state.activeListings.concat(data.items);
    state.listings = state.activeListings;

    if (state.showFavoritesOnly) {
      renderFavoritesView();
    } else {
      renderListings(data.items, reset);
      const shown = state.activeListings.length;
      el.resultTitle.textContent = "Curated PGs for you";
      el.resultCount.textContent = `Showing ${shown} of ${state.total} listings`;
      el.emptyState.classList.toggle("hidden", shown > 0);
      el.loadMore.classList.toggle(
        "hidden",
        shown >= state.total || data.items.length === 0,
      );
    }

    updateHeroMetrics();
  } catch (error) {
    el.resultCount.textContent = "Could not load listings.";
    el.emptyState.classList.remove("hidden");
    el.loadMore.classList.add("hidden");
  } finally {
    state.loading = false;
  }
}

function updateHeroMetrics() {
  const count = state.total;
  el.metricListings.textContent = count;
  if (!state.activeListings.length) {
    el.metricAvgRent.textContent = "Rs 0";
    return;
  }

  const avg =
    state.activeListings.reduce(
      (sum, listing) => sum + listing.startingPrice,
      0,
    ) / state.activeListings.length;
  el.metricAvgRent.textContent = `Rs ${Math.round(avg).toLocaleString("en-IN")}`;
}

function renderListings(items, reset) {
  const template = document.getElementById("listingCardTemplate");

  if (reset) {
    el.listingGrid.innerHTML = "";
  }

  for (const listing of items) {
    const card = template.content.firstElementChild.cloneNode(true);
    const img = card.querySelector("img");
    const badgeWrap = card.querySelector(".badges");
    const saveBtn = card.querySelector(".save-btn");

    img.src = listing.mainImageUrl;
    img.alt = listing.title;

    const badges = [];
    if (listing.verified) badges.push("Verified");
    if (listing.partnerVerified) badges.push("Partner Verified");
    if (listing.brandNew) badges.push("Brand New");
    badges.forEach((badgeText) => {
      const badge = document.createElement("span");
      badge.className = "badge";
      badge.textContent = badgeText;
      badgeWrap.appendChild(badge);
    });

    card.querySelector("h4").textContent = listing.title;
    card.querySelector(".address").textContent =
      `${listing.locality}, ${listing.city}`;
    card.querySelector(".desc").textContent = listing.shortDescription;
    card.querySelector(".price-row").innerHTML =
      `<span>Rs ${listing.startingPrice.toLocaleString("en-IN")} - Rs ${listing.endingPrice.toLocaleString("en-IN")}</span><span>${formatGender(listing.genderType)}</span>`;

    const meta = card.querySelector(".meta");
    meta.appendChild(
      chip(
        `Rating ${listing.ratingAvg?.toFixed(1) || "N/A"} (${listing.reviewCount || 0})`,
      ),
    );
    meta.appendChild(chip(`${listing.availableBeds} beds`));
    if (listing.foodIncluded) meta.appendChild(chip("Food Included"));
    if (listing.nearbyMetro) meta.appendChild(chip(listing.nearbyMetro));

    const amenityWrap = card.querySelector(".amenities");
    (listing.topAmenities || []).forEach((item) =>
      amenityWrap.appendChild(chip(titleCase(item))),
    );

    const detailLink = card.querySelector(".view-btn");
    detailLink.href = `/listings/${listing.slug}`;

    saveBtn.textContent = state.favorites.has(listing.id) ? "Saved" : "Save";
    saveBtn.classList.toggle("saved", state.favorites.has(listing.id));
    saveBtn.addEventListener("click", () =>
      toggleFavorite(listing.id, saveBtn),
    );

    const compareCheck = card.querySelector(".compare-check");
    compareCheck.checked = state.compareIds.has(listing.id);
    compareCheck.addEventListener("change", (event) => {
      if (event.target.checked && state.compareIds.size >= 3) {
        event.target.checked = false;
        showToast("You can compare up to 3 PGs at a time.");
        return;
      }

      if (event.target.checked) {
        state.compareIds.add(listing.id);
      } else {
        state.compareIds.delete(listing.id);
      }
      syncCompareBar();
    });

    el.listingGrid.appendChild(card);
  }

  syncCompareBar();
}

function renderFavoritesView() {
  const favoriteListings = state.activeListings.filter((listing) =>
    state.favorites.has(listing.id),
  );
  el.listingGrid.innerHTML = "";
  renderListings(favoriteListings, true);
  el.resultTitle.textContent = "Your Saved PGs";
  el.resultCount.textContent = `${favoriteListings.length} saved listing(s) from current search`;
  el.emptyState.classList.toggle("hidden", favoriteListings.length > 0);
  el.loadMore.classList.add("hidden");
}

function renderActiveFilterPills(filters) {
  const pills = [];

  if (filters.city) pills.push({ label: `City: ${filters.city}`, key: "city" });
  if (filters.q) pills.push({ label: `Keyword: ${filters.q}`, key: "q" });
  if (filters.gender)
    pills.push({
      label: `Gender: ${formatGender(filters.gender)}`,
      key: "gender",
    });
  if (filters.roomType)
    pills.push({
      label: `Room: ${formatRoomType(filters.roomType)}`,
      key: "roomType",
    });
  if (filters.minRent)
    pills.push({
      label: `Min: Rs ${Number(filters.minRent).toLocaleString("en-IN")}`,
      key: "minRent",
    });
  if (filters.maxRent)
    pills.push({
      label: `Max: Rs ${Number(filters.maxRent).toLocaleString("en-IN")}`,
      key: "maxRent",
    });
  if (filters.foodIncluded) pills.push({ label: "Food Included", key: "food" });
  if (filters.verified) pills.push({ label: "Verified", key: "verified" });

  filters.amenities.forEach((amenity) => {
    pills.push({
      label: `Amenity: ${titleCase(amenity.replace(/_/g, " "))}`,
      key: "amenity",
      value: amenity,
    });
  });

  if (!pills.length) {
    el.activeFilterPills.classList.add("hidden");
    el.activeFilterPills.innerHTML = "";
    return;
  }

  el.activeFilterPills.classList.remove("hidden");
  el.activeFilterPills.innerHTML = pills
    .map(
      (pill) =>
        `<span class="filter-pill">${pill.label}<button type="button" data-pill-key="${pill.key}" data-pill-value="${pill.value || ""}" aria-label="Remove filter">x</button></span>`,
    )
    .join("");

  el.activeFilterPills
    .querySelectorAll("button[data-pill-key]")
    .forEach((button) => {
      button.addEventListener("click", () => {
        clearFilter(button.dataset.pillKey, button.dataset.pillValue);
      });
    });
}

function clearFilter(key, value) {
  if (key === "city") el.cityInput.value = "";
  if (key === "q") el.queryInput.value = "";
  if (key === "gender") el.genderInput.value = "";
  if (key === "roomType") el.roomTypeInput.value = "";
  if (key === "minRent") el.minRentInput.value = "";
  if (key === "maxRent") el.maxRentInput.value = "";
  if (key === "food") el.foodInput.checked = false;
  if (key === "verified") el.verifiedInput.checked = false;
  if (key === "amenity") {
    const amenityInput = document.querySelector(
      `input[name="amenity"][value="${value}"]`,
    );
    if (amenityInput) amenityInput.checked = false;
  }

  fetchListings(true);
}

function chip(text) {
  const node = document.createElement("span");
  node.textContent = text;
  return node;
}

function formatGender(value) {
  if (value === "BOYS") return "Boys";
  if (value === "GIRLS") return "Girls";
  return "Coed";
}

function formatRoomType(value) {
  return value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

function titleCase(text) {
  return text
    .split(" ")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1).toLowerCase())
    .join(" ");
}

function toggleFavorite(listingId, button) {
  if (state.favorites.has(listingId)) {
    state.favorites.delete(listingId);
    showToast("Removed from saved listings.");
  } else {
    state.favorites.add(listingId);
    showToast("Added to saved listings.");
  }
  localStorage.setItem(FAVORITES_KEY, JSON.stringify([...state.favorites]));
  button.textContent = state.favorites.has(listingId) ? "Saved" : "Save";
  button.classList.toggle("saved", state.favorites.has(listingId));
  refreshFavoriteCount();

  if (state.showFavoritesOnly) {
    renderFavoritesView();
  }
}

function refreshFavoriteCount() {
  el.favoriteCount.textContent = state.favorites.size;
}

function syncCompareBar() {
  const count = state.compareIds.size;
  el.compareCount.textContent = `${count} selected`;
  el.compareBar.classList.toggle("hidden", count === 0);
}

function openCompareDialog() {
  if (!state.compareIds.size) return;

  const selected = state.activeListings.filter((listing) =>
    state.compareIds.has(listing.id),
  );
  if (!selected.length) return;

  const headers = selected
    .map((listing) => `<th>${listing.title}</th>`)
    .join("");
  const row = (label, picker) => {
    const cols = selected
      .map((listing) => `<td>${picker(listing)}</td>`)
      .join("");
    return `<tr><th>${label}</th>${cols}</tr>`;
  };

  el.compareContent.innerHTML = `
        <table class="compare-table">
            <thead>
                <tr>
                    <th>Field</th>
                    ${headers}
                </tr>
            </thead>
            <tbody>
                ${row("Price", (x) => `Rs ${x.startingPrice.toLocaleString("en-IN")} - Rs ${x.endingPrice.toLocaleString("en-IN")}`)}
                ${row("Gender", (x) => formatGender(x.genderType))}
                ${row("Rating", (x) => `${x.ratingAvg?.toFixed(1) || "N/A"} / 5`)}
                ${row("Beds", (x) => x.availableBeds)}
                ${row("Food", (x) => (x.foodIncluded ? "Included" : "Extra"))}
                ${row("Verified", (x) => (x.verified ? "Yes" : "No"))}
                ${row("Metro", (x) => x.nearbyMetro || "-")}
            </tbody>
        </table>
    `;

  el.compareDialog.showModal();
}

function openCitySuggestionList() {
  if (!state.citySuggestions.length) return;
  el.citySuggestionList.classList.remove("hidden");
}

function closeCitySuggestionList() {
  el.citySuggestionList.classList.add("hidden");
  state.activeCitySuggestionIndex = -1;
}

function highlightCitySuggestion() {
  const options = [...el.citySuggestionList.querySelectorAll(".city-option")];
  options.forEach((option, index) => {
    option.classList.toggle(
      "active",
      index === state.activeCitySuggestionIndex,
    );
  });
}

function applyCitySuggestion(city) {
  el.cityInput.value = city;
  closeCitySuggestionList();
  fetchListings(true);
}

function renderCitySuggestions(cities) {
  state.citySuggestions = cities;
  state.activeCitySuggestionIndex = -1;

  if (!cities.length) {
    closeCitySuggestionList();
    return;
  }

  el.citySuggestionList.innerHTML = cities
    .map(
      (city, index) =>
        `<button type="button" class="city-option" data-index="${index}" role="option">${city}</button>`,
    )
    .join("");

  el.citySuggestionList.querySelectorAll(".city-option").forEach((button) => {
    button.addEventListener("mousedown", (event) => {
      event.preventDefault();
      const city = state.citySuggestions[Number(button.dataset.index)];
      applyCitySuggestion(city);
    });
  });

  openCitySuggestionList();
}

async function loadCitySuggestions(queryText) {
  const token = ++state.cityRequestToken;
  const query = queryText.trim();

  try {
    const params = new URLSearchParams();
    params.set("limit", "8");
    if (query) params.set("query", query);

    const response = await fetch(`/api/cities?${params.toString()}`);
    if (!response.ok) return;

    const cities = await response.json();
    if (token !== state.cityRequestToken) return;

    if (!query) {
      renderCitySuggestions(cities);
      return;
    }

    const ranked = cities
      .slice()
      .sort(
        (a, b) =>
          Number(!a.toLowerCase().startsWith(query.toLowerCase())) -
          Number(!b.toLowerCase().startsWith(query.toLowerCase())),
      );

    renderCitySuggestions(ranked);
  } catch (error) {
    closeCitySuggestionList();
  }
}

const debouncedCityLookup = debounce(loadCitySuggestions, 180);

function handleCityInputKeyDown(event) {
  const optionsCount = state.citySuggestions.length;

  if (event.key === "ArrowDown" && optionsCount > 0) {
    event.preventDefault();
    state.activeCitySuggestionIndex =
      (state.activeCitySuggestionIndex + 1) % optionsCount;
    highlightCitySuggestion();
    return;
  }

  if (event.key === "ArrowUp" && optionsCount > 0) {
    event.preventDefault();
    state.activeCitySuggestionIndex =
      (state.activeCitySuggestionIndex - 1 + optionsCount) % optionsCount;
    highlightCitySuggestion();
    return;
  }

  if (event.key === "Enter") {
    event.preventDefault();
    if (
      state.activeCitySuggestionIndex >= 0 &&
      state.activeCitySuggestionIndex < optionsCount
    ) {
      applyCitySuggestion(
        state.citySuggestions[state.activeCitySuggestionIndex],
      );
    } else {
      closeCitySuggestionList();
      fetchListings(true);
    }
    return;
  }

  if (event.key === "Escape") {
    closeCitySuggestionList();
  }
}

function setupQuickChips() {
  document.querySelectorAll(".chip[data-chip]").forEach((chipBtn) => {
    chipBtn.addEventListener("click", () => {
      const type = chipBtn.dataset.chip;
      if (type === "verified") el.verifiedInput.checked = true;
      if (type === "food") el.foodInput.checked = true;
      if (type === "girls") el.genderInput.value = "GIRLS";
      if (type === "budget") el.maxRentInput.value = "7000";
      fetchListings(true);
      window.scrollTo({
        top: document.getElementById("listings").offsetTop - 70,
        behavior: "smooth",
      });
    });
  });
}

function setupCityQuickButtons() {
  document.querySelectorAll(".city-quick").forEach((button) => {
    button.addEventListener("click", () => {
      el.cityInput.value = button.dataset.city || "";
      fetchListings(true);
    });
  });
}

function setupEvents() {
  el.applyFilters.addEventListener("click", () => fetchListings(true));

  el.clearFilters.addEventListener("click", () => {
    el.cityInput.value = "";
    el.queryInput.value = "";
    el.genderInput.value = "";
    el.roomTypeInput.value = "";
    el.minRentInput.value = "";
    el.maxRentInput.value = "";
    el.foodInput.checked = false;
    el.verifiedInput.checked = false;
    el.sortInput.value = "RELEVANCE";
    [...document.querySelectorAll('input[name="amenity"]')].forEach((node) => {
      node.checked = false;
    });
    state.showFavoritesOnly = false;
    fetchListings(true);
  });

  el.loadMore.addEventListener("click", () => {
    state.page += 1;
    fetchListings(false);
  });

  el.quickSearchForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const value = el.quickSearchInput.value.trim();
    el.queryInput.value = value;
    fetchListings(true);
  });

  el.queryInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
      event.preventDefault();
      fetchListings(true);
    }
  });

  el.sortInput.addEventListener("change", () => fetchListings(true));

  el.openCompare.addEventListener("click", openCompareDialog);
  el.closeCompare.addEventListener("click", () => el.compareDialog.close());

  el.favoritesBtn.addEventListener("click", () => {
    state.showFavoritesOnly = !state.showFavoritesOnly;
    if (state.showFavoritesOnly) {
      el.favoritesBtn.classList.add("saved");
      renderFavoritesView();
      return;
    }
    el.favoritesBtn.classList.remove("saved");
    fetchListings(true);
  });

  el.cityInput.addEventListener("input", () => {
    debouncedCityLookup(el.cityInput.value);
  });

  el.cityInput.addEventListener("focus", () => {
    debouncedCityLookup(el.cityInput.value);
  });

  el.cityInput.addEventListener("keydown", handleCityInputKeyDown);

  document.addEventListener("click", (event) => {
    if (
      !el.citySuggestionList.contains(event.target) &&
      event.target !== el.cityInput
    ) {
      closeCitySuggestionList();
    }
  });

  setupQuickChips();
  setupCityQuickButtons();
}

setupEvents();
refreshFavoriteCount();
fetchListings(true);
