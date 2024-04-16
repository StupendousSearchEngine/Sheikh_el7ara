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

    const formData = new FormData();
    formData.append('textData',searchTerm);
    console.log("mt"+searchTerm);
    fetch('http://localhost:8080/api/search', {
      method: 'POST',
      body: formData
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // Handle the response from the backend
      console.log(data);
    })
    .catch(error => {
      console.error('There was a problem with the fetch operation:', error);
    });
    //window.location.href = `results.html?searchTerm=${searchTerm}`;
    
  }
  