// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Adds a random greeting to the page. */
function addRandomQuote() {
  const quotes=
    ['Life happens wherever you are, whether you make it or not.', 'Pride is not the opposite of shame, but its source.', 
    'In the darkness, hope is something you give yourself.', 'If you keep moving you will come to a better place.',
    'You must actively shape your own destiny and the destiny of the world.', 'We can\'t concern ourselves with what was. We must act on what is.',
    'Anyone is capable of great good and great evil.', 
    'you must look within yourself to save yourself from your other self, only then will you find your true self reveal itself'];
      
  const buttonText = ['Give me more wisdom', 'MORE!!', 'Enlighten me again!', 'Teach me your ways.', 'Another one!', 'I like that, tell me another.'];

  // Pick a random greeting.
  let quote = quotes[Math.floor(Math.random() * quotes.length)];

  //Pick random button text.
  let genButton = buttonText[Math.floor(Math.random() * buttonText.length)];

  // Retrieve current quote.
  currQuote = document.getElementById('quote-container').innerText;
  currButton= document.getElementById('genquote').innerText;

  // Pick a random greeting until we get a new quote.
  while(currQuote === quote) {
    quote = quotes[Math.floor(Math.random() * quotes.length)];
  }

  //Pick random button text until we get a new message.
  while(currButton === genButton) {
    genButton = buttonText[Math.floor(Math.random() * buttonText.length)];
  }

  //Add new quote and button text to page.
  document.getElementById('quote-container').innerText = quote;
  document.getElementById('genquote').innerText = genButton;
}

/** Create hover effect on given elements. */
function filterOn(element) {
  element.style.filter = "grayscale(100%)";
}

function filterOff(element) {
  element.style.filter = "grayscale(0%)";
}

function largerFont(element) {
  element.style.fontSize = "130%";
}

function normalFont(element) {
  element.style.fontSize = "100%";
}

/** Slideshow. */
var slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

/** Displays slideshow by setting specified dot and picture as active 
  * and all others as inactive. */
function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  if (n > slides.length) {slideIndex = 1}    
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";  
  }
  for (i = 0; i < dots.length; i++) {
    dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";  
  dots[slideIndex-1].className += " active";
}

/** Initializes webpage with updates quotes, login status, and map features. */
function updatePage() {
    updateQuotes();
    fetchLogin();
    initMap();
}

/** Copies specified number of user-created quotes onto site from datastore. */
function updateQuotes(maxQuotes = 5) {
  // Reset quotes
  var quoteDiv = document.getElementById("quote-container");
  quoteDiv.innerHTML='';

  //Write quotes to quoteDiv
  fetch('/data').then(response => response.json()).then((strResponse) => {
    if(strResponse.length < maxQuotes) {maxQuotes = strResponse.length}
    for(i = 0; i < maxQuotes; i++) {
      quoteDiv.innerHTML += strResponse[i];
    }
  });
}

/** Update number of quotes visible on the page */
function updateQuoteNum() {
  updateQuotes(document.getElementById("numQuotes").value);
}

/** Creates an <p> element containing text. */
function createPElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

/** Deletes comments from datastore and refreshes page. */
function deleteComments() {
  fetch('/delete-data');
}

/** Fetches login information and updates html within the
  * loginPrompt div to reflect the login status. */
function fetchLogin() {
    var commentBox = document.getElementById('commentBox');
    var loginPrompt = document.getElementById('loginPrompt');
    loginPrompt.innerHTML = "";
    fetch('/login').then(response => response.json()).then((loginStatus) => {
        if(loginStatus[0] == "True") {
            commentBox.style.display = "block";
        } else {
            commentBox.style.display = "none";
        }
        for(i = 1; i < loginStatus.length; i++) {
            loginPrompt.innerHTML += loginStatus[i];
        }
    });
}

/** Initializes Map API. */
function initMap() {
  var worldCenter = {lat: 40.52, lng: 34.34};

  // Initialize map.
  var map = new google.maps.Map(document.getElementById("map"), {
    center: worldCenter,
    zoom: 3,
    styles: [
      { "elementType": "geometry",
        "stylers": [{"color": "#ebe3cd"}]
      },
      { "elementType": "labels.text.fill",
        "stylers": [{"color": "#523735"}]
      },
      { "elementType": "labels.text.stroke",
        "stylers": [{"color": "#f5f1e6"}]
      },
      { 
        "featureType": "administrative",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#c9b2a6"}]
      },
      {
        "featureType": "administrative.land_parcel",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#dcd2be"}]
      },
      {
        "featureType": "administrative.land_parcel",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#ae9e90"}]
      },
      {
        "featureType": "landscape.natural",
        "elementType": "geometry",
        "stylers": [{"color": "#dfd2ae"}]
      },
      {
        "featureType": "poi",
        "elementType": "geometry",
        "stylers": [{"color": "#dfd2ae"}]
      },
      {
        "featureType": "poi",
        "elementType": "labels.text",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "poi",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#93817c"}]
      },
      {
        "featureType": "poi.business",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "poi.park",
        "elementType": "geometry.fill",
        "stylers": [{"color": "#a5b076"}]
      },
      {
        "featureType": "poi.park",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#447530"}]
      },
      {
        "featureType": "road",
        "elementType": "geometry",
        "stylers": [{"color": "#f5f1e6"}]
      },
      {
        "featureType": "road",
        "elementType": "labels.icon",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "road.arterial",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "road.arterial",
        "elementType": "geometry",
        "stylers": [{"color": "#fdfcf8"}]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry",
        "stylers": [{"color": "#f8c967"}]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#e9bc62"}]
      },
      {
        "featureType": "road.highway",
        "elementType": "labels",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "road.highway.controlled_access",
        "elementType": "geometry",
        "stylers": [{"color": "#e98d58"}]
      },
      {
        "featureType": "road.highway.controlled_access",
        "elementType": "geometry.stroke",
        "stylers": [{"color": "#db8555"}]
      },
      {
        "featureType": "road.local",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "road.local",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#806b63"}]
      },
      {
        "featureType": "transit",
        "stylers": [{"visibility": "off"}]
      },
      {
        "featureType": "transit.line",
        "elementType": "geometry",
        "stylers": [{"color": "#dfd2ae"}]
      },
      {
        "featureType": "transit.line",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#8f7d77"}]
      },
      {
        "featureType": "transit.line",
        "elementType": "labels.text.stroke",
        "stylers": [{"color": "#ebe3cd"}]
      },
      {
        "featureType": "transit.station",
        "elementType": "geometry",
        "stylers": [{"color": "#dfd2ae"}]
      },
      {
        "featureType": "water",
        "elementType": "geometry.fill",
        "stylers": [{"color": "#b9d3c2"}]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [{"color": "#92998d"}]
      }
    ]
  });

  // Initialize marker.
  var markers = initMarkers(map);
}

/** Initializes markers on map. */
function initMarkers(map) {
  var locData = initLocData();
  var markers = [];

  // Initialize a marker for each location.
  locData.forEach((location) => {
    var coord = {lat: location.lat, lng: location.lng};
    var marker = new google.maps.Marker({
        position: coord, 
        map: map,
        animation: google.maps.Animation.DROP
        });

    // Add event listeners to markers.
    marker.addListener('mouseover', () => toggleBounce(marker));
    marker.addListener('mouseout', () => toggleBounce(marker));

    // Push marker into collection.
    markers.push(marker);
  });
  return markers;
}

/** Toggles BOUNCE animation for markers on mouse event. */
function toggleBounce(marker) {
  if (marker.getAnimation() !== null) {
    marker.setAnimation(null);
  } else {
    marker.setAnimation(google.maps.Animation.BOUNCE);
  }
}

// Initialize location coordinates and links.
function initLocData() {
  var locationCoordinates = [ 
    {lat: 20.5937, lng: 78.9629, link: "https://indianaid.carrd.co"}, // India
    {lat: 15.5527, lng: 48.5164, link:  "https://yemencrisis.carrd.co"}, // Yemen
    {lat: 12.8797, lng: 121.7740, link: "https://junkterrorbill.carrd.co"}, // Philippines
    {lat: 22.3193, lng: 114.1694, link: "https://standwithhongkong.carrd.co"}, // Hong Kong
    {lat: 31.9522, lng: 35.2332, link: "https://helppalestine.carrd.co"}, // Palestine
    {lat: 44.9778, lng: -93.2650, link: "https://blacklivesmatter.carrd.co"} // Minneapolis
  ];
  return locationCoordinates;
}
