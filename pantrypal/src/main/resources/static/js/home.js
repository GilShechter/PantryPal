document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const logoutBtn = document.getElementById('logoutBtn');

    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        window.location.href = '/home';
    });

    if (token) {
        // User is authenticated
        document.getElementById('likedNavItem').style.display = 'block';
        document.getElementById('historyNavItem').style.display = 'block';
        document.getElementById('loginNavItem').style.display = 'none';
        document.getElementById('logoutNavItem').style.display = 'block';
    } else {
        // User is not authenticated
        document.getElementById('likedNavItem').style.display = 'none';
        document.getElementById('historyNavItem').style.display = 'none';
        document.getElementById('loginNavItem').style.display = 'block';
        document.getElementById('logoutNavItem').style.display = 'none';
    }

});
