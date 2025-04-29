function goPage(path) {
    if (!path.startsWith('/')) {
        path = '/' + path;
    }
    window.location.href = path;
}