const root = document.querySelector(".detail-page");
const slug = root?.dataset.slug;

const listingState = {
  listing: null,
};

const nodes = {
  detailMainImage: document.getElementById("detailMainImage"),
  detailBadges: document.getElementById("detailBadges"),
  detailTitle: document.getElementById("detailTitle"),
  detailAddress: document.getElementById("detailAddress"),
  detailPrice: document.getElementById("detailPrice"),
  detailMeta: document.getElementById("detailMeta"),
  detailDesc: document.getElementById("detailDesc"),
  detailAmenities: document.getElementById("detailAmenities"),
  detailGallery: document.getElementById("detailGallery"),
  roomTableBody: document.getElementById("roomTableBody"),
  reviewGrid: document.getElementById("reviewGrid"),
  reviewAttribution: document.getElementById("reviewAttribution"),
  googleAttributions: document.getElementById("googleAttributions"),
  contactInfo: document.getElementById("contactInfo"),
  mapLink: document.getElementById("mapLink"),
  inquiryForm: document.getElementById("inquiryForm"),
  reviewForm: document.getElementById("reviewForm"),
  inquiryResponse: document.getElementById("inquiryResponse"),
};

function formatAmenity(value) {
  return value
    .toLowerCase()
    .split("_")
    .map((chunk) => chunk.charAt(0).toUpperCase() + chunk.slice(1))
    .join(" ");
}

function formatRoomType(value) {
  return value
    .toLowerCase()
    .split("_")
    .map((chunk) => chunk.charAt(0).toUpperCase() + chunk.slice(1))
    .join(" ");
}

function formatSource(source) {
  if (source === "GOOGLE_MAPS") return "Google Maps";
  return "StaySaathi";
}

function formatDate(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleDateString();
}

function mergeUniqueUrls(urls) {
  const seen = new Set();
  const merged = [];
  for (const url of urls) {
    if (!url || seen.has(url)) continue;
    seen.add(url);
    merged.push(url);
  }
  return merged;
}

async function loadListing() {
  try {
    const response = await fetch(`/api/listings/${slug}`);
    if (!response.ok) throw new Error("Failed");
    const listing = await response.json();
    listingState.listing = listing;
    renderListing(listing);
  } catch (error) {
    nodes.detailTitle.textContent = "Listing unavailable";
  }
}

function renderListing(listing) {
  const googleData = listing.googlePlaceData;
  const gallery = mergeUniqueUrls([
    ...(googleData?.photoUrls || []),
    ...(listing.galleryImages || []),
  ]);
  const primaryImage = gallery[0] || listing.mainImageUrl;

  nodes.detailMainImage.src = primaryImage;
  nodes.detailMainImage.alt = listing.title;
  nodes.detailTitle.textContent = listing.title;
  nodes.detailAddress.textContent = `${listing.address}`;
  nodes.detailPrice.textContent = `Rs ${listing.startingPrice.toLocaleString("en-IN")} - Rs ${listing.endingPrice.toLocaleString("en-IN")}`;
  nodes.detailDesc.textContent = listing.description;

  const badges = [];
  if (listing.verified) badges.push("Verified");
  if (listing.partnerVerified) badges.push("Partner Verified");
  if (listing.brandNew) badges.push("Brand New");
  if (listing.brandName) badges.push(`Brand: ${listing.brandName}`);
  if (googleData?.reviews?.length) badges.push("Google Reviews Linked");
  nodes.detailBadges.textContent = badges.join(" • ");

  const metaItems = [
    `<span>${listing.genderType}</span>`,
    `<span>Portal ${listing.ratingAvg?.toFixed(1) || "N/A"} (${listing.reviewCount || 0})</span>`,
    `<span>${listing.availableBeds} beds open</span>`,
    listing.foodIncluded
      ? "<span>Food Included</span>"
      : "<span>Food Extra</span>",
    listing.nearbyMetro ? `<span>${listing.nearbyMetro}</span>` : "",
  ];

  if (googleData?.rating != null) {
    metaItems.push(
      `<span>Google ${googleData.rating.toFixed(1)} (${googleData.userRatingsTotal || 0})</span>`,
    );
  }

  nodes.detailMeta.innerHTML = metaItems.join("");

  nodes.detailAmenities.innerHTML = "";
  (listing.amenities || []).forEach((amenity) => {
    const chip = document.createElement("span");
    chip.textContent = formatAmenity(amenity);
    nodes.detailAmenities.appendChild(chip);
  });

  nodes.detailGallery.innerHTML = "";
  gallery.forEach((url) => {
    const image = document.createElement("img");
    image.src = url;
    image.alt = listing.title;
    nodes.detailGallery.appendChild(image);
  });

  nodes.roomTableBody.innerHTML = "";
  (listing.roomOptions || []).forEach((room) => {
    const row = document.createElement("tr");
    row.innerHTML = `
            <td>${formatRoomType(room.roomType)}</td>
            <td>${room.label}</td>
            <td>Rs ${room.price.toLocaleString("en-IN")}</td>
            <td>${room.acIncluded ? "Yes" : "No"}</td>
            <td>${room.attachedWashroom ? "Yes" : "No"}</td>
            <td>${room.availableBeds}</td>
        `;
    nodes.roomTableBody.appendChild(row);
  });

  nodes.contactInfo.textContent = `${listing.contactName} | ${listing.contactPhone}`;

  if (googleData?.mapsUrl) {
    nodes.mapLink.href = googleData.mapsUrl;
    nodes.mapLink.textContent = "Open on Google Maps";
  } else {
    nodes.mapLink.href = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${listing.latitude},${listing.longitude}`)}`;
    nodes.mapLink.textContent = "Open on Map";
  }

  const mergedReviews = [
    ...(googleData?.reviews || []),
    ...(listing.reviews || []),
  ];
  renderReviews(
    mergedReviews,
    Boolean(googleData?.reviews?.length),
    googleData?.htmlAttributions || [],
  );
}

function renderReviews(reviews, hasGoogleReviews, attributions) {
  nodes.reviewGrid.innerHTML = "";

  if (hasGoogleReviews) {
    nodes.reviewAttribution.textContent =
      "Google Maps reviews are fetched live from official Places API when available.";
  } else {
    nodes.reviewAttribution.textContent = "";
  }

  if (attributions.length) {
    nodes.googleAttributions.innerHTML = attributions.join(" • ");
  } else {
    nodes.googleAttributions.textContent = "";
  }

  if (!reviews.length) {
    const empty = document.createElement("p");
    empty.textContent = "No reviews yet. Be the first to review.";
    nodes.reviewGrid.appendChild(empty);
    return;
  }

  reviews.forEach((review) => {
    const card = document.createElement("article");
    card.className = "review-card";

    const name = document.createElement("strong");
    name.textContent = review.reviewerName || "Guest";

    const rating = document.createElement("span");
    const reviewRating =
      typeof review.overallRating === "number"
        ? review.overallRating.toFixed(1)
        : "N/A";
    const source = formatSource(review.source);
    const dateText = formatDate(review.createdAt);
    rating.textContent = `Rating ${reviewRating} • ${source}${dateText ? ` • ${dateText}` : ""}`;

    const comment = document.createElement("p");
    comment.textContent = review.comment || "No comment provided.";

    card.appendChild(name);
    card.appendChild(rating);
    card.appendChild(comment);
    nodes.reviewGrid.appendChild(card);
  });
}

async function handleInquirySubmit(event) {
  event.preventDefault();
  if (!listingState.listing) return;

  const body = {
    listingId: listingState.listing.id,
    name: document.getElementById("inqName").value,
    phone: document.getElementById("inqPhone").value,
    email: document.getElementById("inqEmail").value || null,
    moveInDate: document.getElementById("inqMoveIn").value || null,
    preferredRoomType: document.getElementById("inqRoomType").value || null,
    budget: Number(document.getElementById("inqBudget").value) || null,
    expectedStayMonths:
      Number(document.getElementById("inqStay").value) || null,
    message: document.getElementById("inqMessage").value || null,
  };

  try {
    const response = await fetch("/api/inquiries", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Failed");
    nodes.inquiryResponse.textContent = data.message;
    nodes.inquiryForm.reset();
  } catch (error) {
    nodes.inquiryResponse.textContent =
      error.message || "Could not submit inquiry";
  }
}

async function handleReviewSubmit(event) {
  event.preventDefault();

  const body = {
    reviewerName: document.getElementById("reviewerName").value,
    overallRating: Number(document.getElementById("overallRating").value),
    locationRating: Number(document.getElementById("locationRating").value),
    staffRating: Number(document.getElementById("staffRating").value),
    foodRating: Number(document.getElementById("foodRating").value),
    cleanlinessRating: Number(
      document.getElementById("cleanlinessRating").value,
    ),
    wifiRating: Number(document.getElementById("wifiRating").value),
    comment: document.getElementById("reviewComment").value,
  };

  try {
    const response = await fetch(`/api/listings/${slug}/reviews`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Failed");
    document.getElementById("reviewForm").reset();
    await loadListing();
  } catch (error) {
    alert(error.message || "Could not submit review");
  }
}

nodes.inquiryForm?.addEventListener("submit", handleInquirySubmit);
nodes.reviewForm?.addEventListener("submit", handleReviewSubmit);
loadListing();
