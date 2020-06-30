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

/**
 * Adds a random greeting to the page.
 */
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

/* Slideshow. */
var slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

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

function fetchRandQuote() {
    fetch('/data').then(response => response.json()).then((quotes) => {
        document.getElementById('quote-container').innerText = quotes[1]
    });
}

function addComment() {
    fetch('/data').then(response => response.json()).then((comment) => {
        document.getElementById('quote-container').innerText = comment;
    });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
