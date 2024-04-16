const inputField = document.getElementById('textInput');
const suggestionDropdown = document.getElementById('suggestionDropdown');

// Function to generate and populate the dropdown menu with suggestions
function populateDropdown(suggestions) {
  suggestionDropdown.innerHTML = ''; // Clear existing suggestions

  suggestions.forEach(suggestion => {
    const listItem = document.createElement('li');
    listItem.textContent = suggestion;
    listItem.addEventListener('click', function() {
      // Handle click on a suggestion (e.g., pre-populate input)
      inputField.value = suggestion;
      suggestionDropdown.classList.remove('show'); // Hide dropdown on selection
    });
    suggestionDropdown.appendChild(listItem);
  });

  if (suggestions.length > 0) {
    suggestionDropdown.classList.add('show'); // Show dropdown if suggestions exist
  } else {
    suggestionDropdown.classList.remove('show'); // Hide dropdown on no suggestions
  }
}

// Function to suggest words based on the input
function getSuggestions(inputText) {
  
  const wordList = ['apple', 'banana', 'orange', 'grapefruit', 'mango'];
  if (inputText)
  {
  const suggestions = wordList.filter(word => word.startsWith(inputText.toLowerCase()));
  return suggestions;
  }
  else
  return [];
}

// Function to show/hide the dropdown based on input
function handleInput(event) {
  const enteredText = event.target.value.toLowerCase();
  const suggestions = getSuggestions(enteredText);
if (suggestions.length<0)
    suggestionDropdown.style.visibility='hidden';
else
    suggestionDropdown.style.visibility='visible';

  populateDropdown(suggestions);

}

// Attach event listener to the input field
inputField.addEventListener('input', handleInput);
