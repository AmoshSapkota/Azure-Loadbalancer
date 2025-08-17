

function fetchProducts() {
  fetch('/products')
    .then(res => res.json())
    .then(products => {
      const list = document.getElementById('product-list');
      if (!Array.isArray(products) || products.length === 0) {
        list.innerHTML = '<div class="empty">No products found.</div>';
        return;
      }
      let table = `<table><tr><th>ID</th><th>Name</th><th>Price</th><th>Category</th><th>Actions</th></tr>`;
      products.forEach(p => {
        table += `
        <tr>
          <td>${p.prodId}</td>
          <td>${p.prodName}</td>
          <td>${p.price}</td>
          <td>${p.category ? p.category : ''}</td>
          <td>
            <button onclick="showUpdateForm(${p.prodId}, '${p.prodName.replace(/'/g, "\\'")}', ${p.price}, '${p.category ? p.category.replace(/'/g, "\\'") : ''}')">Edit</button>
            <button onclick="deleteProduct(${p.prodId})">Delete</button>
          </td>
        </tr>`;
      });
      table += '</table>';
      list.innerHTML = table;
    })
    .catch(() => {
      document.getElementById('product-list').innerHTML = '<div class="empty">Failed to load products.</div>';
    });
}

function addProduct(event) {
  event.preventDefault();
  const prodName = document.getElementById('add-prodName').value;
  const price = document.getElementById('add-price').value;
  const category = document.getElementById('add-category').value;
  fetch('/products', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ prodName, price, category })
  })
    .then(res => res.text())
    .then(msg => {
      showMessage(msg, 'green');
      fetchProducts();
      document.getElementById('add-form').reset();
    })
    .catch(() => showMessage('Failed to add product', 'red'));
}

function deleteProduct(prodId) {
  fetch(`/products/${prodId}`, { method: 'DELETE' })
    .then(res => res.text())
    .then(msg => {
      showMessage(msg, 'red');
      fetchProducts();
    })
    .catch(() => showMessage('Failed to delete product', 'red'));
}

function showUpdateForm(prodId, prodName, price, category) {
  document.getElementById('update-prodId').value = prodId;
  document.getElementById('update-prodName').value = prodName;
  document.getElementById('update-price').value = price;
  document.getElementById('update-category').value = category || '';
  document.getElementById('update-form').style.display = 'block';
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function hideUpdateForm() {
  document.getElementById('update-form').style.display = 'none';
}

function showPatchForm(prodId) {
  document.getElementById('patch-prodId').value = prodId;
  document.getElementById('patch-prodName').value = '';
  document.getElementById('patch-price').value = '';
  document.getElementById('patch-category').value = '';
  document.getElementById('patch-imageUrl').value = '';
  document.getElementById('patch-form').style.display = 'block';
  document.getElementById('update-form').style.display = 'none';
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function hidePatchForm() {
  document.getElementById('patch-form').style.display = 'none';
}

function updateProduct(event) {
  event.preventDefault();
  const prodId = document.getElementById('update-prodId').value;
  const prodName = document.getElementById('update-prodName').value;
  const price = document.getElementById('update-price').value;
  const category = document.getElementById('update-category').value;
  fetch(`/products/${prodId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ prodId, prodName, price, category })
  })
    .then(res => res.text())
    .then(msg => {
      showMessage(msg, 'blue');
      fetchProducts();
      document.getElementById('update-form').style.display = 'none';
    })
    .catch(() => showMessage('Failed to update product', 'red'));
}

function patchProduct(event) {
  event.preventDefault();
  const prodId = document.getElementById('patch-prodId').value;
  const prodName = document.getElementById('patch-prodName').value;
  const price = document.getElementById('patch-price').value;
  const category = document.getElementById('patch-category').value;
  const imageUrl = document.getElementById('patch-imageUrl').value;
  const patchData = {};
  if (prodName) patchData.prodName = prodName;
  if (price) patchData.price = price;
  if (category) patchData.category = category;
  if (imageUrl) patchData.imageUrl = imageUrl;
  fetch(`/products/${prodId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(patchData)
  })
    .then(res => res.text())
    .then(msg => {
      showMessage(msg, 'orange');
      fetchProducts();
      document.getElementById('patch-form').style.display = 'none';
    })
    .catch(() => showMessage('Failed to patch product', 'red'));
}

document.getElementById('add-form').addEventListener('submit', addProduct);
document.getElementById('update-form').addEventListener('submit', updateProduct);
document.getElementById('patch-form').addEventListener('submit', patchProduct);
document.getElementById('update-form').style.display = 'none';
document.getElementById('patch-form').style.display = 'none';

function showMessage(msg, color) {
  const m = document.getElementById('message');
  m.textContent = msg;
// ...existing code...
document.getElementById('add-form').addEventListener('submit', addProduct);
document.getElementById('update-form').addEventListener('submit', updateProduct);
document.getElementById('update-form').style.display = 'none';

function showMessage(msg, color) {
  const m = document.getElementById('message');
  m.textContent = msg;
  m.style.color = color;
  setTimeout(() => { m.textContent = ''; }, 2500);
}

window.onload = fetchProducts;
