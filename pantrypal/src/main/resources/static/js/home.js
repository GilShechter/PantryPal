document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');

    if (token) {
        // User is authenticated
        document.getElementById('likedNavItem').style.display = 'block';
        document.getElementById('historyNavItem').style.display = 'block';
        document.getElementById('loginNavItem').style.display = 'none';
    } else {
        // User is not authenticated
        document.getElementById('likedNavItem').style.display = 'none';
        document.getElementById('historyNavItem').style.display = 'none';
        document.getElementById('loginNavItem').style.display = 'block';
    }

});
