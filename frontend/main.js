const API_URL = 'http://localhost:8080/api';

// Tab Logic
document.querySelectorAll('.tab').forEach(tab => {
  tab.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    
    tab.classList.add('active');
    document.getElementById(tab.dataset.target).classList.add('active');
  });
});

// Books Logic
const loadBooks = async () => {
  const keyword = document.getElementById('searchBook').value;
  const res = await fetch(`${API_URL}/books?keyword=${keyword}`);
  const books = await res.json();
  const tbody = document.getElementById('booksTableBody');
  tbody.innerHTML = '';
  
  books.forEach(book => {
    // book.available could be boolean or integer depending on MySQL driver
    const isAvail = book.available === true || book.available === 1;
    const status = isAvail 
      ? '<span class="status-badge status-available">Available</span>' 
      : '<span class="status-badge status-borrowed">Borrowed</span>';
      
    tbody.innerHTML += `
      <tr>
        <td>${book.book_id}</td>
        <td>${book.book_name}</td>
        <td>${book.author}</td>
        <td>${status}</td>
        <td>
          <button class="action-btn delete-book" data-id="${book.book_id}">🗑️</button>
        </td>
      </tr>
    `;
  });

  document.querySelectorAll('.delete-book').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      if(confirm('Delete book?')) {
        await fetch(`${API_URL}/books/${e.target.dataset.id}`, { method: 'DELETE' });
        loadBooks();
      }
    });
  });
};

document.getElementById('searchBook').addEventListener('keyup', loadBooks);

document.getElementById('addBookForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const payload = {
    bookName: document.getElementById('bookName').value,
    author: document.getElementById('bookAuthor').value,
    isbn: document.getElementById('bookIsbn').value,
    genre: document.getElementById('bookGenre').value
  };
  
  await fetch(`${API_URL}/books`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  
  e.target.reset();
  loadBooks();
});

// Users Logic
const loadUsers = async () => {
  const res = await fetch(`${API_URL}/users`);
  const users = await res.json();
  const tbody = document.getElementById('usersTableBody');
  tbody.innerHTML = '';
  
  users.forEach(user => {
    tbody.innerHTML += `
      <tr>
        <td>${user.user_id}</td>
        <td>${user.name}</td>
        <td>${user.email}</td>
        <td>${user.borrowed_count}</td>
      </tr>
    `;
  });
};

document.getElementById('addUserForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const payload = {
    name: document.getElementById('userName').value,
    email: document.getElementById('userEmail').value
  };
  
  await fetch(`${API_URL}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  
  e.target.reset();
  loadUsers();
});

// Borrowing Logic
document.getElementById('btnBorrow').addEventListener('click', async () => {
  const payload = {
    userId: document.getElementById('borrowUserId').value,
    bookId: document.getElementById('borrowBookId').value
  };
  
  const res = await fetch(`${API_URL}/borrow/checkout`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  const data = await res.json();
  const resultDiv = document.getElementById('borrowResult');
  resultDiv.innerHTML = `<p style="color: ${data.success ? 'var(--secondary)' : 'var(--danger)'}">${data.message}</p>`;
  
  loadBooks();
  loadUsers();
});

document.getElementById('btnReturn').addEventListener('click', async () => {
  const payload = {
    userId: document.getElementById('borrowUserId').value,
    bookId: document.getElementById('borrowBookId').value
  };
  
  const res = await fetch(`${API_URL}/borrow/return`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  const data = await res.json();
  const resultDiv = document.getElementById('borrowResult');
  resultDiv.innerHTML = `<p style="color: ${data.success ? 'var(--secondary)' : 'var(--danger)'}">${data.message}</p>`;
  
  loadBooks();
  loadUsers();
});

// Init
loadBooks();
loadUsers();
