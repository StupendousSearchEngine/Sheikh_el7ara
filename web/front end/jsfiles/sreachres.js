const searchResultsContainer = document.getElementById('search-results');
const nextPageBtn = document.getElementById('next-page-btn');
const prevPageBtn = document.getElementById('prev-page-btn');
const resultsList = [];

const nump = 40;
const numResults = 40;
const numPagesTotal=Math.ceil(numResults/10);  
let pageNumber=1;
function getSearchResults(pageNumber) {

}

// Initial page number (adjust as needed)
let currentPage = 1;

function displaySearchResults(resultsList) {
  searchResultsContainer.innerHTML = ''; // Clear previous results
    const myList=[...resultsList[currentPage-1]];
    console.log("mylist"+myList)
    const listLenght=myList.length;
    for(let i=0;    i < listLenght;   i++) {
        
        result=myList.shift();


        const card = document.createElement('div');
        card.classList.add('card', 'mb-3');

        const cardBody = document.createElement('div');
        cardBody.classList.add('card-body');

        const cardTitle = document.createElement('h5');
        cardTitle.classList.add('card-title');
        cardTitle.textContent = result.title;

        const cardLink = document.createElement('a');
        cardLink.classList.add('link');
        cardLink.textContent = result.link;

        const cardText = document.createElement('p');
        cardText.classList.add('card-text');
        cardText.textContent = result.description;

        cardBody.appendChild(cardTitle);
        cardBody.appendChild(cardLink);
        cardBody.appendChild(cardText);
        card.appendChild(cardBody);

        searchResultsContainer.appendChild(card);
        
    };
}

// Display initial results on page load
displaySearchResults(getSearchResults(currentPage));

nextPageBtn.addEventListener('click', () => {
  
 

  // Check if there are more results to display
  if (resultsList.length==0 || currentPage >= numPagesTotal) {
    nextPageBtn.disabled = true; 
  } else {
    currentPage++;
    displaySearchResults(resultsList);
    
    prevPageBtn.disabled = false; 
}
console.log(pageNumber);
});
prevPageBtn.addEventListener('click',() =>{

    
    if (currentPage<=1) {
        prevPageBtn.disabled = true; 
    
      } else {
        
        currentPage--;
        displaySearchResults(resultsList);
        nextPageBtn.disabled = false; // Disable button if no more pages
       
      }
      console.log(pageNumber);
})