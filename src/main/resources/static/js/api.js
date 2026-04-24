/* ═══════════════════════════════════════════════════════════════
   api.js  —  HTTP-клиент с JWT-авторизацией
   ═══════════════════════════════════════════════════════════════ */

const API = (() => {
    const BASE = '';

    /* ── Token helpers ─────────────────────────────────────── */
    function getToken()          { return localStorage.getItem('token'); }
    function setToken(t)         { localStorage.setItem('token', t); }
    function removeToken()       { localStorage.removeItem('token'); }
    function getUser()           { const u = localStorage.getItem('user'); return u ? JSON.parse(u) : null; }
    function setUser(u)          { localStorage.setItem('user', JSON.stringify(u)); }
    function removeUser()        { localStorage.removeItem('user'); }
    function isLoggedIn()        { return !!getToken(); }

    /* ── Core fetch wrapper ───────────────────────────────── */
    async function request(method, url, body) {
        const headers = { 'Content-Type': 'application/json' };
        const token = getToken();
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const opts = { method, headers };
        if (body && method !== 'GET') opts.body = JSON.stringify(body);

        const res = await fetch(BASE + url, opts);

        /* 401 — токен истёк или невалидный */
        if (res.status === 401) {
            removeToken();
            removeUser();
            window.location.reload();
            throw new Error('Сессия истекла. Войдите заново.');
        }

        const text = await res.text();
        let data;
        try { data = JSON.parse(text); } catch { data = text; }

        if (!res.ok) {
            const msg = (data && data.message) ? data.message : 'Ошибка сервера';
            throw new Error(msg);
        }
        return data;
    }

    /* ── Shorthand methods ────────────────────────────────── */
    const get    = (url)       => request('GET', url);
    const post   = (url, body) => request('POST', url, body);
    const put    = (url, body) => request('PUT', url, body);
    const patch  = (url, body) => request('PATCH', url, body);
    const del    = (url)       => request('DELETE', url);

    /* ── Auth ─────────────────────────────────────────────── */
    async function login(username, password) {
        const res = await post('/api/auth/login', { username, password });
        const auth = res.data;               // ApiResponse → data = AuthResponse
        setToken(auth.token);
        setUser({
            id:       auth.userId,
            username: auth.username,
            fullName: auth.fullName,
            role:     auth.role
        });
        return auth;
    }

    function logout() {
        removeToken();
        removeUser();
    }

    /* ── Public API ───────────────────────────────────────── */
    return {
        getToken, getUser, isLoggedIn,
        login, logout,
        get, post, put, patch, del
    };
})();
