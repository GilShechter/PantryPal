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
    const likeBtn = document.getElementById('likeBtn');
    const userId = localStorage.getItem('userId');
    let recipeId;
    let liked = false;


    const heartFillHtml = "<img src=\"/images/heartFill.png\" alt=\"Like\">";
    const heartEmptyHtml = "<img src=\"/images/heartEmpty.png\" alt=\"Like\">";

    // Clear previous results
    ingredientsList.innerHTML = '';
    recipesList.innerHTML = '';

    // Show the spinner
    uploadSection.style.display = 'none';
    spinner.style.display = 'block';

    let requestImageUrl;

    try {
        if (imageFile) {
            const base64Image = await readFileAsBase64(imageFile);
            const requestData = { imageFile: base64Image };

            // Upload the image to get ingredients
            const uploadResponse = await fetch('/api/identifier/imageFile', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData),
            });

            if (!uploadResponse.ok) {
                throw new Error('Failed to analyze image');
            }

            const data = await uploadResponse.json();
            requestImageUrl = data.imageUrl;
        } else if (imageUrl) {
            requestImageUrl = imageUrl;
        } else {
            throw new Error('Please provide an image URL or upload a file.');
        }

        console.log('Request image URL:', requestImageUrl);
        const requestData = { imageUrl: requestImageUrl };
        // Analyze the image to get ingredients
        const response = await fetch('/api/identifier', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData),
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to analyze image: ${response.status} ${errorText}`);
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
                <div class="col-sm-5">
                  <img src="${recipe.image}" class="img-fluid rounded-start" alt="${recipe.title}">
                </div>
                <div class="col-sm-7">
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
                recipeId = this.getAttribute('data-recipe-id');
                showRecipeDetails(recipeId);
            });
        });

    } catch (error) {
        ingredientsList.innerHTML = `<p>Error: ${error.message}</p>`;
    } finally {
        // Hide the spinner
        spinner.style.display = 'none';
    }

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
            const recipeJson = await response.json();

            console.log('Response:', recipeJson);

            let recipe;

            if (!recipeJson.body) {
                recipe = recipeJson;
            } else {
                recipe = recipeJson.body;
            }


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

            if (userId) {
                await createRecipe(recipe);
                await viewRecipe(recipeId, userId);
                likeBtn.style.display = 'block';
            }
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

    document.getElementById('likeBtn').addEventListener('click', async () => {
        if (!liked) {
            document.getElementById('likeBtn').innerHTML = heartFillHtml;
            liked = true;
            await likeRecipe(recipeId);
        } else {
            document.getElementById('likeBtn').innerHTML = heartEmptyHtml;
            liked = false;
            await unlikeRecipe(recipeId);
        }
    });

    async function createRecipe(recipe) {
        try {
            const response = await fetch('/api/recipes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(recipe),
            });

            if (!response.ok) {
                throw new Error('Failed to create recipe');
            }

            const data = await response.json();
            console.log('Recipe created:', data);
        } catch (error) {
            console.error('Error creating recipe:', error);
        }
    }

    async function viewRecipe(recipeId, userId) {
        try {
            const response = await fetch(`/api/user/${userId}/view?recipeId=${recipeId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to view recipe');
            }

            const data = await response.json();
            console.log('Recipe viewed:', data);
        } catch (error) {
            console.error('Error viewing recipe:', error);
        }
    }

    async function likeRecipe(recipeId) {
        try {
            const response = await fetch(`/api/user/${userId}/like?recipeId=${recipeId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to like recipe');
            }

            const data = await response.json();
            console.log('Recipe liked:', data);
        } catch (error) {
            console.error('Error liking recipe:', error);
        }
    }

    async function unlikeRecipe(recipeId) {
        try {
            const response = await fetch(`/api/user/${userId}/unlike?recipeId=${recipeId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error('Failed to unlike recipe');
            }

            const data = await response.json();
            console.log('Recipe unliked:', data);
        } catch (error) {
            console.error('Error unliking recipe:', error);
        }
    }

    function readFileAsBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onloadend = () => {
                const base64String = reader.result.split(',')[1]; // Get base64 part
                resolve(base64String);
            };
            reader.onerror = error => reject(error);
        });
    }

});