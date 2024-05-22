document.getElementById('uploadForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const imageUrl = document.getElementById('imageUrl').value;
    const ingredientsList = document.getElementById('ingredientsList');
    const recipesList = document.getElementById('recipesList');

    // Clear previous results
    ingredientsList.innerHTML = '';
    recipesList.innerHTML = '';

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
        const ingredientsHtml = `<h2>Ingredients:</h2><ul>${ingredients.map(ingredient => `<li>${ingredient}</li>`).join('')}</ul>`;
        ingredientsList.innerHTML = ingredientsHtml;

        // Fetch recipes based on ingredients
        const recipesResponse = await fetch(`/api/recipes/searchByIngredients?ingredients=${ingredients.toString()}`);

        if (!recipesResponse.ok) {
            throw new Error('Failed to fetch recipes');
        }

        const recipes = await recipesResponse.json();

        // Display recipes
        const recipesHtml = `<h2>Recipes:</h2><ul>${recipes.map(recipe => `<li>${recipe.title}</li>`).join('')}</ul>`;
        recipesList.innerHTML = recipesHtml;

    } catch (error) {
        ingredientsList.innerHTML = `<p>Error: ${error.message}</p>`;
    }
});
