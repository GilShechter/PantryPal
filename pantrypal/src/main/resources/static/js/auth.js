document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');

    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const username = document.getElementById('userNameInput').value;
            const password = document.getElementById('passwordInput').value;

            const response = await fetch('/authenticate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                localStorage.setItem('userId', data.id);
                window.location.href = '/home'; // Redirect to home page
            } else {
                document.getElementById('login-fail-message').innerText = 'Sign in failed. Please try again.';
            }
        });
    }

    if (signupForm) {
        signupForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const username = document.getElementById('userNameInput').value;
            const password = document.getElementById('passwordInput').value;
            const confirmPassword = document.getElementById('confirmPasswordInput').value;

            if (password !== confirmPassword) {
                document.getElementById('signup-fail-message').innerText = 'Passwords do not match. Please try again.';
                return;
            }

            const response = await fetch('/user', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                localStorage.setItem('userId', data.id);
                window.location.href = '/home'; // Redirect to home page
            } else {
                document.getElementById('signup-fail-message').innerText = 'Signup failed. Please try again.';
            }
        });
    }
});
