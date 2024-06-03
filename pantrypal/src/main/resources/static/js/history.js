document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');

    const title = document.title;

    const viewedRecipesSection = document.getElementById('viewedRecipesSection');
    const spinner = document.getElementById('spinner');
    const recipeDetailsCard = document.getElementById('recipeDetailsCard');
    const likeBtn = document.getElementById('likeBtn');

    const heartFillHtml = "<img src=\"/images/heartFill.png\" alt=\"Like\">";
    const heartEmptyHtml = "<img src=\"/images/heartEmpty.png\" alt=\"Like\">";
    let action;
  
    if (viewedRecipesSection) {
      title === 'History - PantryPal' ? action = 'viewed' : action = 'liked';
      fetchViewedRecipes();
    }
  
    async function fetchViewedRecipes() {
      try {
        spinner.style.display = 'block';
        const response = await fetch(`api/user/${userId}/${action}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
  
        if (response.ok) {
          const userRecipes = await response.json();
          const recipes = await getRecipesInfo(userRecipes);
          console.log(recipes);
          displayRecipes(recipes);
        } else {
          console.error('Failed to fetch viewed recipes.');
        }
      } catch (error) {
        console.error('Error:', error);
      } finally {
        spinner.style.display = 'none';
      }
    }

    async function getRecipesInfo(userRecipes) {
        const recipePromises = userRecipes.map(async recipe => {
            const response = await fetch(`/api/recipes/getRecipeInfo?id=${recipe.recipeId}`);
            if (!response.ok) {
            throw new Error('Failed to fetch recipe details');
            }
            const recipeInfo = await response.json();
            return {
                ...recipeInfo,
                liked: recipe.liked
            };
        });
        return Promise.all(recipePromises);
    }
  
    function displayRecipes(recipes) {
      const viewedRecipesList = document.getElementById('viewedRecipesList');
      viewedRecipesList.innerHTML = '';
  
      recipes.forEach(recipe => {
        const recipeElement = document.createElement('div');
        recipeElement.className = 'recipe-preview';
        recipeElement.innerHTML = `
            <div class="card mb-3 recipe-card" style="max-width: 540px;" data-recipe-id="${recipe.body.id}">
            <img src="${recipe.body.image}" class="img-fluid rounded-start" alt="${recipe.body.title}">
                <div class="card-body">
                <h5 class="card-title">${recipe.body.title}</h5>
                <p class="card-text">Ready in: ${recipe.body.readyInMinutes} minutes</p>
                </div>
            </div>
        `;
  
        recipeElement.addEventListener('click', () => {
          showRecipeDetails(recipe);
        });
  
        viewedRecipesList.appendChild(recipeElement);
      });
    }
  
    function showRecipeDetails(recipe) {
      document.getElementById('detailsImage').src = recipe.body.image;
      document.getElementById('detailsTitle').textContent = recipe.body.title;
      document.getElementById('detailsReadyInMinutes').textContent = recipe.body.readyInMinutes;
      document.getElementById('detailsInstructions').textContent = recipe.body.instructions;
      document.getElementById('detailsSourceUrl').href = recipe.body.sourceUrl;
      document.getElementById('detailsSourceUrl').textContent = recipe.body.sourceUrl;
      document.getElementById('detailsIngredients').textContent = recipe.body.extendedIngredients.join(', ');
      recipe.liked == true ? likeBtn.innerHTML = heartFillHtml : likeBtn.innerHTML = heartEmptyHtml;
      viewedRecipesSection.style.display = 'none';
      recipeDetailsCard.style.display = 'block';
    }
  
    document.getElementById('closeDetailsBtn').addEventListener('click', () => {
      recipeDetailsCard.style.display = 'none';
      viewedRecipesSection.style.display = 'block';
    });

    document.getElementById('likeBtn').addEventListener('click', async () => {
      if (document.getElementById('likeBtn').innerHTML === heartEmptyHtml) {
        document.getElementById('likeBtn').innerHTML = heartFillHtml;
        await likeRecipe();
      } else {
        document.getElementById('likeBtn').innerHTML = heartEmptyHtml;
        await unlikeRecipe();
      }
    });

    async function likeRecipe() {
      const recipeId = document.querySelector('.recipe-preview .recipe-card').getAttribute('data-recipe-id');
      const response = await fetch(`/api/user/${userId}/like?recipeId=${recipeId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) {
        console.error('Failed to like recipe.');
      }
    }

    async function unlikeRecipe() {
        const recipeId = document.querySelector('.recipe-preview .recipe-card').getAttribute('data-recipe-id');
        const response = await fetch(`/api/user/${userId}/unlike?recipeId=${recipeId}`, {
            method: 'POST',
            headers: {
            'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            console.error('Failed to unlike recipe.');
        }
    }

  });
  