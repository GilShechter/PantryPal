document.getElementById('uploadForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const imageUrl = document.getElementById('imageUrl').value;
    const imageFile = document.getElementById('imageFile').files[0];
    const ingredientsList = document.getElementById('ingredientsList');
    const recipesList = document.getElementById('recipesList');
    const spinner = document.getElementById('spinner');
    const uploadSection = document.getElementById('uploadSection');
    const recipeDetailsCard = document.getElementById('recipeDetailsCard');
    const closeDetailsBtn = document.getElementById('closeDetailsBtn');

    // Clear previous results
    ingredientsList.innerHTML = '';
    recipesList.innerHTML = '';

    // Show the spinner
    uploadSection.style.display = 'none';
    spinner.style.display = 'block';

    let requestImageUrl;

    try {
        if (imageFile) {
            let formData = new FormData();
            formData.append('imageFile', imageFile);
            const uploadResponse = await fetch('/api/identifier/imageFile', {
                method: 'POST',
                body: formData,
            });

            if (!uploadResponse.ok) {
                throw new Error('Failed to analyze image');
            }
            const reader = uploadResponse.body.getReader();
            const decoder = new TextDecoder('utf-8');
            let result = '';
            while (true) {
                const { done, value } = await reader.read();
                if (done) {
                    break;
                }
                result += decoder.decode(value);
            }
            requestImageUrl = result;
        } else if (imageUrl) {
            requestImageUrl = imageUrl;
        } else {
            throw new Error('Please provide an image URL or upload a file.');
        }

        console.log('Request image URL:', requestImageUrl);
        const formData = new FormData();
        formData.append('imageUrl', requestImageUrl);
        // Analyze the image to get ingredients
        const response = await fetch('/api/identifier', {
            method: 'POST',
            body: formData,
        });

        if (!response.ok) {
            throw new Error('Failed to analyze image');
        }

        const data = await response.json();
        const ingredients = data.ingredients;

        // Display ingredients
        const ingredientsHtml = `<h2>Searching recipes for the ingredients:</h2><p>${ingredients.join(', ')}</p>`;
        ingredientsList.innerHTML = ingredientsHtml;

        // Fetch recipes based on ingredients
        const recipesResponse = await fetch(`/api/recipes/searchByIngredients?ingredients=${ingredients.toString()}`);

        if (!recipesResponse.ok) {
            throw new Error('Failed to fetch recipes');
        }

        const recipes = await recipesResponse.json();

        // Display recipes using horizontal Bootstrap cards
        const recipesHtml = recipes.map(recipe => `
            <div class="card mb-3 recipe-card" style="max-width: 540px;" data-recipe-id="${recipe.id}">
              <div class="row g-0">
                <div class="col-md-4">
                  <img src="${recipe.image}" class="img-fluid rounded-start" alt="${recipe.title}">
                </div>
                <div class="col-md-8">
                  <div class="card-body">
                    <h5 class="card-title">${recipe.title}</h5>
                    <p class="card-text">Likes: ${recipe.likes}<br>Used Ingredients: ${recipe.usedIngredientCount}<br>Missed Ingredients: ${recipe.missedIngredientCount}</p>
                  </div>
                </div>
              </div>
            </div>
        `).join('');
        recipesList.innerHTML = recipesHtml;

        // Add event listeners to recipe cards
        document.querySelectorAll('.recipe-card').forEach(card => {
            card.addEventListener('click', function() {
                const recipeId = this.getAttribute('data-recipe-id');
                showRecipeDetails(recipeId);
            });
        });

    } catch (error) {
        ingredientsList.innerHTML = `<p>Error: ${error.message}</p>`;
    } finally {
        // Hide the spinner
        spinner.style.display = 'none';
    }
});

async function showRecipeDetails(recipeId) {
    try {
        ingredientsList.style.display = 'none';
        recipesList.style.display = 'none';
        spinner.style.display = 'block';

        // Fetch recipe information from the server
        const response = await fetch(`/api/recipes/getRecipeInfo?id=${recipeId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch recipe details');
        }
        const recipe = await response.json();

        // Populate the expandable card with recipe details
        document.getElementById('detailsTitle').textContent = recipe.title;
        document.getElementById('detailsImage').src = recipe.image;
        document.getElementById('detailsImage').alt = recipe.title;
        document.getElementById('detailsReadyInMinutes').textContent = recipe.readyInMinutes;
        document.getElementById('detailsInstructions').textContent = recipe.instructions;
        document.getElementById('detailsSourceUrl').href = recipe
        document.getElementById('detailsSourceUrl').textContent = recipe.sourceUrl;
        document.getElementById('detailsIngredients').textContent = recipe.extendedIngredients.join(', ');

        // Show the expandable card
        recipeDetailsCard.style.display = 'block';
    } catch (error) {
        console.error('Error displaying recipe details:', error);
    } finally {
        // Hide the spinner
        spinner.style.display = 'none';
    }
}

closeDetailsBtn.addEventListener('click', function() {
    // Hide the details card and show the recipes section
    recipeDetailsCard.style.display = 'none';
    recipesList.style.display = 'block';
    ingredientsList.style.display = 'block';
});

