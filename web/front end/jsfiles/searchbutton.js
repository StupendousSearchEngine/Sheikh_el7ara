const searchForm = document.getElementById('textInput');
const searchButton = document.getElementById('searchButton');
searchButton.addEventListener('click', function(event) {
    event.preventDefault(); 
    const searchTerm =  document.getElementById('textInput').value;
    console.log("text val"+searchTerm);
    if(searchTerm.length>0)
        handleSearch(searchTerm);
  });
  
  async function handleSearch(searchTerm) {
    
    window.location.href = `results.html?searchTerm=${searchTerm}`;
    
  }
  