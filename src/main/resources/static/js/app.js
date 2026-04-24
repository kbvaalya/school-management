/* ═══════════════════════════════════════════════════════════
   app.js  —  School Management SPA
   ═══════════════════════════════════════════════════════════ */

let currentPage = 'dashboard';

/* ── Toast ────────────────────────────────────────────────── */
function showToast(msg, type = 'info') {
    const c = document.getElementById('toastContainer');
    const t = document.createElement('div');
    t.className = 'toast ' + type;
    t.textContent = msg;
    c.appendChild(t);
    setTimeout(() => t.remove(), 3000);
}

/* ── Login ────────────────────────────────────────────────── */
async function handleLogin(e) {
    e.preventDefault();
    const btn = document.getElementById('loginBtn');
    btn.disabled = true;
    btn.querySelector('span').textContent = 'Загрузка…';
    try {
        const u = document.getElementById('loginUsername').value.trim();
        const p = document.getElementById('loginPassword').value;
        await API.login(u, p);
        showToast('Добро пожаловать!', 'success');
        initApp();
    } catch (err) {
        showToast(err.message || 'Ошибка входа', 'error');
    } finally {
        btn.disabled = false;
        btn.querySelector('span').textContent = 'Войти';
    }
    return false;
}

function logout() {
    API.logout();
    document.getElementById('appLayout').style.display = 'none';
    document.getElementById('loginPage').style.display = '';
    document.getElementById('loginForm').reset();
}

/* ── Init App ─────────────────────────────────────────────── */
function initApp() {
    const user = API.getUser();
    if (!user) return;
    document.getElementById('loginPage').style.display = 'none';
    document.getElementById('appLayout').style.display = 'flex';
    document.getElementById('userName').textContent = user.fullName;
    document.getElementById('userRole').textContent = user.role.replace('ROLE_', '');
    document.getElementById('userAvatar').textContent = user.fullName.charAt(0);
    buildNav(user.role);
    navigate('dashboard');
}

/* ── Navigation ───────────────────────────────────────────── */
function buildNav(role) {
    const nav = document.getElementById('sidebarNav');
    const items = [{ id: 'dashboard', icon: '📊', label: 'Панель' }];
    if (role === 'ROLE_ADMIN') {
        items.push({ id: 'users', icon: '👥', label: 'Пользователи' });
        items.push({ id: 'classes', icon: '🏫', label: 'Классы' });
        items.push({ id: 'attendance', icon: '📋', label: 'Посещаемость' });
    } else if (role === 'ROLE_MANAGER') {
        items.push({ id: 'classes', icon: '🏫', label: 'Мои классы' });
        items.push({ id: 'attendance', icon: '📋', label: 'Посещаемость' });
    } else {
        items.push({ id: 'classes', icon: '🏫', label: 'Мои классы' });
        items.push({ id: 'attendance', icon: '📋', label: 'Моя посещаемость' });
    }
    nav.innerHTML = items.map(i =>
        `<a class="nav-item" data-page="${i.id}" onclick="navigate('${i.id}')">
            <span class="icon">${i.icon}</span><span>${i.label}</span></a>`
    ).join('');
}

function navigate(page) {
    currentPage = page;
    document.querySelectorAll('.nav-item').forEach(n =>
        n.classList.toggle('active', n.dataset.page === page));
    const main = document.getElementById('mainContent');
    main.innerHTML = '<div class="spinner"></div>';
    const role = API.getUser().role;
    if (page === 'dashboard') loadDashboard(role);
    else if (page === 'users') loadUsers();
    else if (page === 'classes') loadClasses(role);
    else if (page === 'attendance') loadAttendance(role);
}

/* ── Helpers ──────────────────────────────────────────────── */
function roleBadge(r) {
    const s = r.replace('ROLE_', '').toLowerCase();
    return `<span class="badge badge-${s}">${s}</span>`;
}
function statusBadge(s) {
    return `<span class="badge badge-${s.toLowerCase()}">${s}</span>`;
}
function h(s) {
    const d = document.createElement('div'); d.textContent = s || ''; return d.innerHTML;
}
function closeModal() {
    const o = document.getElementById('modalOverlay');
    if (o) o.remove();
}
function apiBase() {
    const role = API.getUser().role;
    return role === 'ROLE_ADMIN' ? '/api/admin' : '/api/manager';
}

/* ══════════════════════════════════════════════════════════
   DASHBOARD
   ══════════════════════════════════════════════════════════ */
async function loadDashboard(role) {
    const main = document.getElementById('mainContent');
    try {
        let stats = [];
        if (role === 'ROLE_ADMIN') {
            const [usersRes, classesRes] = await Promise.all([
                API.get('/api/admin/users'), API.get('/api/admin/classes')
            ]);
            const users = usersRes.data, classes = classesRes.data;
            stats = [
                { icon: '👥', label: 'Пользователи', value: users.length, color: 'purple' },
                { icon: '🧑‍💼', label: 'Менеджеры', value: users.filter(u => u.role === 'ROLE_MANAGER').length, color: 'blue' },
                { icon: '🎓', label: 'Студенты', value: users.filter(u => u.role === 'ROLE_USER').length, color: 'green' },
                { icon: '🏫', label: 'Классы', value: classes.length, color: 'red' }
            ];
        } else if (role === 'ROLE_MANAGER') {
            const classesRes = await API.get('/api/manager/classes');
            stats = [
                { icon: '🏫', label: 'Мои классы', value: classesRes.data.length, color: 'purple' },
                { icon: '🎓', label: 'Студентов', value: classesRes.data.reduce((s, c) => s + c.studentCount, 0), color: 'green' }
            ];
        } else {
            const [classesRes, attRes] = await Promise.all([
                API.get('/api/student/classes'), API.get('/api/student/attendances')
            ]);
            stats = [
                { icon: '🏫', label: 'Мои классы', value: classesRes.data.length, color: 'purple' },
                { icon: '📋', label: 'Записей', value: attRes.data.length, color: 'blue' }
            ];
        }
        main.innerHTML = `
            <div class="page-header"><h1>Панель управления</h1></div>
            <div class="stats-grid">${stats.map(s => `
                <div class="stat-card ${s.color} animate-in">
                    <div class="stat-icon">${s.icon}</div>
                    <div class="stat-label">${s.label}</div>
                    <div class="stat-value ${s.color}">${s.value}</div>
                </div>`).join('')}
            </div>`;
    } catch (err) { main.innerHTML = `<p style="color:var(--danger)">${h(err.message)}</p>`; }
}

/* ══════════════════════════════════════════════════════════
   USERS (Admin only)
   ══════════════════════════════════════════════════════════ */
async function loadUsers() {
    const main = document.getElementById('mainContent');
    try {
        const users = (await API.get('/api/admin/users')).data;
        main.innerHTML = `
            <div class="page-header">
                <h1>Пользователи</h1>
                <div class="actions"><button class="btn btn-primary" onclick="showCreateUserModal()">+ Добавить</button></div>
            </div>
            <div class="card"><div class="table-wrapper"><table>
                <thead><tr><th>ID</th><th>Имя</th><th>Логин</th><th>Email</th><th>Роль</th><th>Действия</th></tr></thead>
                <tbody>${users.map(u => `<tr>
                    <td>${u.id}</td><td>${h(u.fullName)}</td><td>${h(u.username)}</td>
                    <td>${h(u.email)}</td><td>${roleBadge(u.role)}</td>
                    <td><button class="btn btn-danger btn-sm" onclick="deleteUser(${u.id})">🗑</button></td>
                </tr>`).join('')}</tbody>
            </table></div></div>`;
    } catch (err) { main.innerHTML = `<p style="color:var(--danger)">${h(err.message)}</p>`; }
}

function showCreateUserModal() {
    let overlay = document.getElementById('modalOverlay'); if (overlay) overlay.remove();
    overlay = document.createElement('div'); overlay.id = 'modalOverlay';
    overlay.className = 'modal-overlay active';
    overlay.innerHTML = `<div class="modal">
        <div class="modal-header"><h3>Новый пользователь</h3><button class="modal-close" onclick="closeModal()">&times;</button></div>
        <div class="modal-body">
            <div class="form-group"><label>ФИО</label><input class="form-control" id="mFullName"></div>
            <div class="form-group"><label>Email</label><input class="form-control" id="mEmail" type="email"></div>
            <div class="form-group"><label>Роль</label>
                <select class="form-control" id="mRole">
                    <option value="ROLE_MANAGER">Менеджер</option>
                    <option value="ROLE_USER">Студент</option>
                </select></div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Отмена</button>
            <button class="btn btn-primary" onclick="createUser()">Создать</button>
        </div></div>`;
    document.body.appendChild(overlay);
}

async function createUser() {
    try {
        const res = await API.post('/api/admin/users', {
            fullName: document.getElementById('mFullName').value,
            email: document.getElementById('mEmail').value,
            role: document.getElementById('mRole').value
        });
        const d = res.data;
        closeModal();
        showToast('Пользователь создан!', 'success');
        if (d.generatedPassword) {
            let ov = document.createElement('div'); ov.id = 'modalOverlay';
            ov.className = 'modal-overlay active';
            ov.innerHTML = `<div class="modal">
                <div class="modal-header"><h3>Данные для входа</h3><button class="modal-close" onclick="closeModal()">&times;</button></div>
                <div class="modal-body"><div class="credentials-box">
                    <h4>Сохраните данные</h4>
                    <code>Логин: ${h(d.username)}</code>
                    <code>Пароль: ${h(d.generatedPassword)}</code>
                </div></div>
                <div class="modal-footer"><button class="btn btn-primary" onclick="closeModal()">OK</button></div>
            </div>`;
            document.body.appendChild(ov);
        }
        loadUsers();
    } catch (err) { showToast(err.message, 'error'); }
}

async function deleteUser(id) {
    if (!confirm('Удалить пользователя?')) return;
    try { await API.del('/api/admin/users/' + id); showToast('Удалён', 'success'); loadUsers(); }
    catch (err) { showToast(err.message, 'error'); }
}

/* ══════════════════════════════════════════════════════════
   CLASSES (Admin + Manager)
   ══════════════════════════════════════════════════════════ */
async function loadClasses(role) {
    const main = document.getElementById('mainContent');
    try {
        let classes;
        const isAdmin = role === 'ROLE_ADMIN';
        const isManager = role === 'ROLE_MANAGER';
        if (isAdmin) classes = (await API.get('/api/admin/classes')).data;
        else if (isManager) classes = (await API.get('/api/manager/classes')).data;
        else classes = (await API.get('/api/student/classes')).data;

        const canManage = isAdmin || isManager;
        const addBtn = canManage ? `<button class="btn btn-primary" onclick="showCreateClassModal()">+ Класс</button>` : '';
        main.innerHTML = `
            <div class="page-header"><h1>Классы</h1><div class="actions">${addBtn}</div></div>
            <div class="card"><div class="table-wrapper"><table>
                <thead><tr><th>ID</th><th>Название</th><th>Предмет</th><th>Менеджер</th><th>Студентов</th>
                    ${canManage ? '<th>Действия</th>' : ''}</tr></thead>
                <tbody>${classes.map(c => `<tr>
                    <td>${c.id}</td><td>${h(c.name)}</td><td>${h(c.subject || '—')}</td>
                    <td>${c.manager ? h(c.manager.fullName) : '—'}</td><td>${c.studentCount}</td>
                    ${canManage ? `<td style="display:flex;gap:4px">
                        <button class="btn btn-icon" onclick="viewClass(${c.id})" title="Подробнее">👁</button>
                        <button class="btn btn-success btn-sm" onclick="showEnrollModal(${c.id})" title="Добавить студента">➕</button>
                        ${isAdmin ? `<button class="btn btn-danger btn-sm" onclick="deleteClass(${c.id})">🗑</button>` : ''}
                    </td>` : ''}
                </tr>`).join('')}</tbody>
            </table></div></div>`;
    } catch (err) { main.innerHTML = `<p style="color:var(--danger)">${h(err.message)}</p>`; }
}

function showCreateClassModal() {
    let overlay = document.getElementById('modalOverlay'); if (overlay) overlay.remove();
    overlay = document.createElement('div'); overlay.id = 'modalOverlay';
    overlay.className = 'modal-overlay active';
    overlay.innerHTML = `<div class="modal">
        <div class="modal-header"><h3>Новый класс</h3><button class="modal-close" onclick="closeModal()">&times;</button></div>
        <div class="modal-body">
            <div class="form-group"><label>Название *</label><input class="form-control" id="mcName" placeholder="Введите название класса"></div>
            <div class="form-group"><label>Предмет</label><input class="form-control" id="mcSubject" placeholder="Необязательно"></div>
            <div class="form-group"><label>Описание</label><input class="form-control" id="mcDesc" placeholder="Необязательно"></div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Отмена</button>
            <button class="btn btn-primary" onclick="createClass()">Создать</button>
        </div></div>`;
    document.body.appendChild(overlay);
}

async function createClass() {
    try {
        const body = {
            name: document.getElementById('mcName').value,
            subject: document.getElementById('mcSubject').value || null,
            description: document.getElementById('mcDesc').value || null
        };
        await API.post(apiBase() + '/classes', body);
        closeModal();
        showToast('Класс создан!', 'success');
        loadClasses(API.getUser().role);
    } catch (err) { showToast(err.message, 'error'); }
}

async function deleteClass(id) {
    if (!confirm('Удалить класс?')) return;
    try { await API.del('/api/admin/classes/' + id); showToast('Удалён', 'success'); loadClasses('ROLE_ADMIN'); }
    catch (err) { showToast(err.message, 'error'); }
}

/* ── View class detail with student management ─────────── */
async function viewClass(id) {
    try {
        const c = (await API.get(apiBase() + '/classes/' + id)).data;
        let overlay = document.getElementById('modalOverlay'); if (overlay) overlay.remove();
        overlay = document.createElement('div'); overlay.id = 'modalOverlay';
        overlay.className = 'modal-overlay active';
        const students = c.students ? Array.from(c.students) : [];
        overlay.innerHTML = `<div class="modal">
            <div class="modal-header"><h3>${h(c.name)}</h3><button class="modal-close" onclick="closeModal()">&times;</button></div>
            <div class="modal-body">
                <p><strong>Предмет:</strong> ${h(c.subject || '—')}</p>
                <p><strong>Описание:</strong> ${h(c.description || '—')}</p>
                <p><strong>Менеджер:</strong> ${c.manager ? h(c.manager.fullName) : '—'}</p>
                <h4 style="margin-top:16px">Студенты (${students.length})</h4>
                ${students.length ? `<table><thead><tr><th>ID</th><th>Имя</th><th>Email</th><th></th></tr></thead>
                <tbody>${students.map(s => `<tr><td>${s.id}</td><td>${h(s.fullName)}</td><td>${h(s.email)}</td>
                    <td><button class="btn btn-danger btn-sm" onclick="removeStudentFromClass(${c.id},${s.id})">✕</button></td></tr>`).join('')}</tbody></table>`
                : '<p class="empty-state">Нет студентов</p>'}
            </div>
            <div class="modal-footer">
                <button class="btn btn-success" onclick="closeModal();showEnrollModal(${c.id})">➕ Добавить студента</button>
                <button class="btn btn-secondary" onclick="closeModal()">Закрыть</button>
            </div>
        </div>`;
        document.body.appendChild(overlay);
    } catch (err) { showToast(err.message, 'error'); }
}

/* ── Enroll student modal ─────────────────────────────── */
async function showEnrollModal(classId) {
    try {
        const base = apiBase();
        let students;
        if (API.getUser().role === 'ROLE_ADMIN') {
            students = (await API.get('/api/admin/users?role=ROLE_USER')).data;
        } else {
            students = (await API.get('/api/manager/students')).data;
        }
        let overlay = document.getElementById('modalOverlay'); if (overlay) overlay.remove();
        overlay = document.createElement('div'); overlay.id = 'modalOverlay';
        overlay.className = 'modal-overlay active';
        overlay.innerHTML = `<div class="modal">
            <div class="modal-header"><h3>Добавить студента в класс</h3><button class="modal-close" onclick="closeModal()">&times;</button></div>
            <div class="modal-body">
                <div class="form-group"><label>Студент</label>
                    <select class="form-control" id="enrollStudentId">
                        ${students.length ? students.map(s => `<option value="${s.id}">${h(s.fullName)} (${h(s.email)})</option>`).join('')
                        : '<option value="">Нет студентов</option>'}
                    </select></div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal()">Отмена</button>
                <button class="btn btn-primary" onclick="enrollStudent(${classId})">Добавить</button>
            </div></div>`;
        document.body.appendChild(overlay);
    } catch (err) { showToast(err.message, 'error'); }
}

async function enrollStudent(classId) {
    const studentId = document.getElementById('enrollStudentId').value;
    if (!studentId) return showToast('Выберите студента', 'error');
    try {
        await API.post(apiBase() + '/classes/' + classId + '/students/' + studentId, {});
        closeModal();
        showToast('Студент добавлен!', 'success');
        loadClasses(API.getUser().role);
    } catch (err) { showToast(err.message, 'error'); }
}

async function removeStudentFromClass(classId, studentId) {
    if (!confirm('Удалить студента из класса?')) return;
    try {
        await API.del(apiBase() + '/classes/' + classId + '/students/' + studentId);
        closeModal();
        showToast('Студент удалён из класса', 'success');
        loadClasses(API.getUser().role);
    } catch (err) { showToast(err.message, 'error'); }
}

/* ══════════════════════════════════════════════════════════
   ATTENDANCE
   ══════════════════════════════════════════════════════════ */
async function loadAttendance(role) {
    const main = document.getElementById('mainContent');
    try {
        let records;
        if (role === 'ROLE_ADMIN') records = (await API.get('/api/admin/attendances')).data;
        else if (role === 'ROLE_MANAGER') records = (await API.get('/api/manager/attendances')).data;
        else records = (await API.get('/api/student/attendances')).data;

        const canMark = role === 'ROLE_MANAGER';
        const markBtn = canMark ? `<button class="btn btn-primary" onclick="showMarkAttendanceModal()">+ Отметить</button>` : '';
        main.innerHTML = `
            <div class="page-header"><h1>Посещаемость</h1><div class="actions">${markBtn}</div></div>
            <div class="card"><div class="table-wrapper"><table>
                <thead><tr><th>Дата</th><th>Студент</th><th>Класс</th><th>Статус</th><th>Комментарий</th></tr></thead>
                <tbody>${records.length ? records.map(r => `<tr>
                    <td>${r.date}</td><td>${h(r.studentName)}</td><td>${h(r.className)}</td>
                    <td>${statusBadge(r.status)}</td><td>${h(r.comment || '')}</td>
                </tr>`).join('') : '<tr><td colspan="5" class="empty-state">Нет записей</td></tr>'}</tbody>
            </table></div></div>`;
    } catch (err) { main.innerHTML = `<p style="color:var(--danger)">${h(err.message)}</p>`; }
}

async function showMarkAttendanceModal() {
    try {
        const classes = (await API.get('/api/manager/classes')).data;
        let overlay = document.getElementById('modalOverlay'); if (overlay) overlay.remove();
        overlay = document.createElement('div'); overlay.id = 'modalOverlay';
        overlay.className = 'modal-overlay active';
        overlay.innerHTML = `<div class="modal">
            <div class="modal-header"><h3>Отметить посещаемость</h3><button class="modal-close" onclick="closeModal()">&times;</button></div>
            <div class="modal-body">
                <div class="form-group"><label>Класс</label>
                    <select class="form-control" id="maClass" onchange="loadClassStudents()">
                        <option value="">Выберите класс</option>
                        ${classes.map(c => `<option value="${c.id}">${h(c.name)}</option>`).join('')}
                    </select></div>
                <div class="form-group"><label>Студент</label>
                    <select class="form-control" id="maStudent"><option value="">Сначала выберите класс</option></select></div>
                <div class="form-group"><label>Дата</label><input class="form-control" id="maDate" type="date" value="${new Date().toISOString().slice(0,10)}"></div>
                <div class="form-group"><label>Статус</label>
                    <select class="form-control" id="maStatus">
                        <option value="PRESENT">Присутствует</option>
                        <option value="ABSENT">Отсутствует</option>
                        <option value="EXCUSED">Уважительная</option>
                    </select></div>
                <div class="form-group"><label>Комментарий</label><input class="form-control" id="maComment"></div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal()">Отмена</button>
                <button class="btn btn-primary" onclick="markAttendance()">Сохранить</button>
            </div></div>`;
        document.body.appendChild(overlay);
    } catch (err) { showToast(err.message, 'error'); }
}

async function loadClassStudents() {
    const classId = document.getElementById('maClass').value;
    const sel = document.getElementById('maStudent');
    if (!classId) { sel.innerHTML = '<option value="">Сначала выберите класс</option>'; return; }
    try {
        const cls = (await API.get('/api/manager/classes/' + classId)).data;
        const students = cls.students ? Array.from(cls.students) : [];
        sel.innerHTML = students.length
            ? students.map(s => `<option value="${s.id}">${h(s.fullName)}</option>`).join('')
            : '<option value="">Нет студентов</option>';
    } catch (err) { showToast(err.message, 'error'); }
}

async function markAttendance() {
    try {
        await API.post('/api/manager/attendances', {
            classId: parseInt(document.getElementById('maClass').value),
            studentId: parseInt(document.getElementById('maStudent').value),
            date: document.getElementById('maDate').value,
            status: document.getElementById('maStatus').value,
            comment: document.getElementById('maComment').value || null
        });
        closeModal(); showToast('Посещаемость отмечена!', 'success');
        loadAttendance('ROLE_MANAGER');
    } catch (err) { showToast(err.message, 'error'); }
}

/* ── Bootstrap ────────────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
    if (API.isLoggedIn()) initApp();
});
