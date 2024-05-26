document.getElementById('uploadForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const imageUrl = document.getElementById('imageUrl').value;
    const ingredientsList = document.getElementById('ingredientsList');
    const recipesList = document.getElementById('recipesList');
    const spinner = document.getElementById('spinner');
    const uploadSection = document.getElementById('uploadSection');
    const recipeDetailsCard = document.getElementById('recipeDetailsCard');


    // Clear previous results
    ingredientsList.innerHTML = '';
    recipesList.innerHTML = '';

    // Show the spinner
    uploadSection.style.display = 'none';
    spinner.style.display = 'block';

    try {
        // Analyze the image to get ingredients
        const response = await fetch('/api/identifier', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ imageUrl }),
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
        $('#detailsTitle').text(recipe.title);
        $('#detailsImage').attr('src', recipe.image).attr('alt', recipe.title);
        $('#detailsReadyInMinutes').text(recipe.readyInMinutes);
        $('#detailsInstructions').text(recipe.instructions);
        $('#detailsSourceUrl').attr('href', recipe.sourceUrl).text(recipe.sourceUrl);
        $('#detailsIngredients').text(recipe.extendedIngredients.join(', '));

        // Show the expandable card
        recipeDetailsCard.style.display = 'block';
    } catch (error) {
        console.error('Error displaying recipe details:', error);
    } finally {
        // Hide the spinner
        spinner.style.display = 'none';
    }
}

$('#closeDetailsBtn').on('click', function() {
    // Hide the details card and show the recipes section
    recipeDetailsCard.style.display = 'none';
    recipesList.style.display = 'block';
    ingredientsList.style.display = 'block';
});