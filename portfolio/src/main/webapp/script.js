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
      'Anyone is capable of great good and great evil.'];
      
  const buttonText = ['Give me more wisdom', 'MORE!!', 'Enlighten me again!', 'Teach me your ways.', 'Another one!', 'I like that, tell me another.'];

  // Pick a random greeting.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  //Pick random button text
  const genButton = buttonText[Math.floor(Math.random() * buttonText.length)];

  // Add it to the page.
  document.getElementById('quote-container').innerText = quote;
  document.getElementById('genquote').innerText = genButton;
}
