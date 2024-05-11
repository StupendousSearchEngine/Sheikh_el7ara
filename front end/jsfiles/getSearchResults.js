const searchResultsContainer = document.getElementById("search-results");
const nextPageBtn = document.getElementById("next-page-btn");
const prevPageBtn = document.getElementById("prev-page-btn");
var searchTerm;
let resultsList = [];

let numPagesTotal = 0;
function truncateText(text, maxLength) {
  // Check if the text length is greater than the maximum length
  if (text.length > maxLength) {
    // Find the last space within the first maxLength characters
    var lastSpaceIndex = text.lastIndexOf(" ", maxLength);
    // Truncate the text at the last space index
    text = text.substring(0, lastSpaceIndex) + "...";
  }
  return text;
}
function makeWordBoldId(textElementId, wordToBold) {
  wordToBold = wordToBold.replace(/['"]/g, "");
  console.log(wordToBold);
  var textElement = document.getElementById(textElementId);
  var text = textElement.innerHTML;

  var regex = new RegExp("\\b(" + wordToBold + ")\\b", "gi");

  var newText = text.replace(regex, "<b>$1</b>");

  textElement.innerHTML = newText;
}

async function getSearchResults() {
  const currentURL = window.location.href;

  // Extract search term from the URL
  const urlSearchParams = new URLSearchParams(currentURL.split("?")[1]);
  searchTerm = urlSearchParams.get("searchTerm");

  // Example JavaScript code to make an AJAX request
  fetch(`http://localhost:8080/query/${searchTerm};`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((datta) => {
      console.log(datta);

      const countItems = Object.keys(datta).length;
      numPagesTotal = Math.ceil(countItems / 10);
      let displayItems = countItems;
      //let myData= datta.data;
      let myData = Object.entries(datta);

      console.log(numPagesTotal);
      for (let i = 0; i < numPagesTotal; i++) {
        loopCounter = 0;
        if (displayItems > 10) loopCounter = 10;
        else loopCounter = displayItems;
        let result = [];
        for (let j = 0; j < loopCounter; j++) {
          const [firstKey, firstValue] = myData.shift();
          result.push([firstKey, firstValue]);
        }
        resultsList.push(result);
        console.log(resultsList);
      }
      displaySearchResults();
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
}

// Initial page number (adjust as needed)
let currentPage = 1;

async function displaySearchResults() {
  searchResultsContainer.innerHTML = ""; // Clear previous results
  console.log(resultsList);
  let myList = [];

  if (resultsList.length > 0) myList = [...resultsList[currentPage - 1]];
  console.log("mylist" + myList);
  const listLenght = myList.length;
  console.log(listLenght);
  for (let i = 0; i < listLenght; i++) {
    result = myList.shift();
    let url = result[0];
    let content = result[1];

    const path = url.split("/").pop();

    // Convert the path to title format
    const title = path
      .split("-")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ");

    const card = document.createElement("div");
    card.classList.add("card", "mb-3");

    const cardBody = document.createElement("div");
    cardBody.classList.add("card-body");

    const cardTitle = document.createElement("h5");
    cardTitle.classList.add("card-title");
    //cardTitle.textContent = result.title;
    cardTitle.textContent = title;

    console.log("result" + result);
    const cardLink = document.createElement("a");
    cardLink.classList.add("link");
    //cardLink.textContent = result.url;
    cardLink.textContent = url;
    cardLink.href = url;

    const cardText = document.createElement("p");
    cardText.classList.add("card-text");
    cardText.id = "page_content";
    cardText.innerHTML = truncateText(content, 700);
    //cardText.innerHTML = makeWordBold(cardText.innerHTML, searchTerm);
    cardBody.appendChild(cardTitle);
    cardBody.appendChild(cardLink);
    cardBody.appendChild(cardText);

    card.appendChild(cardBody);

    searchResultsContainer.appendChild(card);
    makeWordBoldId("page_content", searchTerm);
    searchResultsContainer.replaceChild(card, card);
  }
}

getSearchResults();
nextPageBtn.addEventListener("click", () => {
  // Check if there are more results to display
  if (resultsList.length == 0 || currentPage >= numPagesTotal) {
    nextPageBtn.disabled = true;
  } else {
    currentPage++;
    displaySearchResults(resultsList);

    prevPageBtn.disabled = false;
  }
  console.log(currentPage);
});
prevPageBtn.addEventListener("click", () => {
  if (currentPage <= 1) {
    prevPageBtn.disabled = true;
  } else {
    currentPage--;
    displaySearchResults(resultsList);
    nextPageBtn.disabled = false; // Disable button if no more pages
  }
  console.log(currentPage);
});
