const searchResultsContainer = document.getElementById('search-results');
const nextPageBtn = document.getElementById('next-page-btn');
const prevPageBtn = document.getElementById('prev-page-btn');
let resultsList = [];



let numPagesTotal=0; 

async function getSearchResults() {

  // Example JavaScript code to make an AJAX request
fetch('http://localhost:8080/api/40')
.then(response => {
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return response.json();
})
.then(datta => {
 
 
  console.log(datta); 
  

  
  const countItems=datta.count;
  numPagesTotal=Math.ceil(countItems/10);
  let displayItems=countItems;
  let myData= datta.data;

  console.log(numPagesTotal);
  for (let i=0;i<numPagesTotal;i++)
  {
    loopCounter =0;
    if (displayItems>10)
      loopCounter=10;
    else
      loopCounter=displayItems;
    let result=[]
    for (let j=0;j<loopCounter;j++)
    {
      
      result.push( myData.shift());
      
    }
    resultsList.push(result);
    console.log(resultsList);
  }
  displaySearchResults();
  
})
.catch(error => {
  console.error('There was a problem with the fetch operation:', error);
});


}

// Initial page number (adjust as needed)
let currentPage = 1;

async function displaySearchResults() {
  searchResultsContainer.innerHTML = ''; // Clear previous results
  console.log(resultsList);

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
        cardLink.textContent = result.url;

        const cardText = document.createElement('p');
        cardText.classList.add('card-text');
        cardText.textContent = result.body;

        cardBody.appendChild(cardTitle);
        cardBody.appendChild(cardLink);
        cardBody.appendChild(cardText);
        card.appendChild(cardBody);

        searchResultsContainer.appendChild(card);
        
    };
}

getSearchResults();
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