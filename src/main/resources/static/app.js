const API = '';

function switchTab(name) {
  document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('nav button').forEach(b => b.classList.remove('active'));
  document.getElementById('tab-' + name).classList.add('active');
  event.target.classList.add('active');
}

async function apiFetch(path, options = {}) {
  const res = await fetch(API + path, {
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    ...options
  });
  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }
  if (!res.ok) throw { status: res.status, data };
  return data;
}

function showAlert(id, msg, type = 'error') {
  const el = document.getElementById(id);
  el.innerHTML = `<div class="alert alert-${type}">${escHtml(msg)}</div>`;
  setTimeout(() => { if (el) el.innerHTML = ''; }, 6000);
}

function setStatus(id, html) {
  const el = document.getElementById(id);
  if (el) el.innerHTML = html;
  setTimeout(() => { if (el) el.innerHTML = ''; }, 4000);
}

function escHtml(s) {
  return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}

function badgeHtml(status) {
  if (!status) return '<span class="badge badge-default">—</span>';
  const s = status.toLowerCase();
  let cls = 'badge-default';
  if (['active','delivered','online'].includes(s)) cls = 'badge-active';
  else if (['inactive','cancelled','offline'].includes(s)) cls = 'badge-inactive';
  else if (['pending','processing','in_transit','shipped'].includes(s)) cls = 'badge-pending';
  return `<span class="badge ${cls}">${escHtml(status)}</span>`;
}

function idCell(id) {
  return `<td class="id-cell" title="${escHtml(id)}">${escHtml(id)}</td>`;
}

function renderTable(cols, rows, mapper) {
  if (!rows || rows.length === 0) {
    return '<div class="empty-state"><div class="icon">&#128203;</div><p>No records found</p></div>';
  }
  const headers = cols.map(c => `<th>${escHtml(c)}</th>`).join('');
  const body = rows.map(r => `<tr>${mapper(r)}</tr>`).join('');
  return `<div class="table-wrapper"><table><thead><tr>${headers}</tr></thead><tbody>${body}</tbody></table></div>`;
}

async function listOrganizations() {
  try {
    const data = await apiFetch('/organizations');
    const rows = Array.isArray(data) ? data : (data.content || data.data || []);
    document.getElementById('org-table').innerHTML = renderTable(
      ['ID', 'Name', 'Status', 'Created'],
      rows,
      r => `${idCell(r.id || r.organizationId || '')}
            <td>${escHtml(r.name || '')}</td>
            <td>${badgeHtml(r.status)}</td>
            <td>${escHtml(r.createdAt ? r.createdAt.split('T')[0] : '—')}</td>`
    );
  } catch (e) {
    showAlert('org-alert', `Failed to load organizations: ${e.status || ''} ${JSON.stringify(e.data || e.message || e)}`);
  }
}

async function createOrganization(e) {
  e.preventDefault();
  const body = { name: document.getElementById('org-name').value, status: document.getElementById('org-status').value };
  try {
    const data = await apiFetch('/organizations', { method: 'POST', body: JSON.stringify(body) });
    setStatus('org-create-status', '<span style="color:var(--success)">&#10003; Created</span>');
    e.target.reset();
    listOrganizations();
  } catch (err) {
    setStatus('org-create-status', `<span style="color:var(--danger)">&#10007; ${err.status || 'Error'}</span>`);
    showAlert('org-alert', `Create failed: ${JSON.stringify(err.data || err.message || err)}`);
  }
}

async function listWebsites() {
  const orgId = document.getElementById('ws-org-id').value.trim();
  if (!orgId) { showAlert('ws-alert', 'Please enter an Organization ID'); return; }
  try {
    const data = await apiFetch(`/organizations/${encodeURIComponent(orgId)}/websites`);
    const rows = Array.isArray(data) ? data : (data.content || data.data || []);
    document.getElementById('ws-table').innerHTML = renderTable(
      ['ID', 'Name', 'Domain', 'Platform', 'Status'],
      rows,
      r => `${idCell(r.id || r.websiteId || '')}
            <td>${escHtml(r.name || '')}</td>
            <td>${escHtml(r.domain || '')}</td>
            <td>${escHtml(r.platform || '')}</td>
            <td>${badgeHtml(r.status)}</td>`
    );
  } catch (e) {
    showAlert('ws-alert', `Failed to load websites: ${e.status || ''} ${JSON.stringify(e.data || e.message || e)}`);
  }
}

async function createWebsite(e) {
  e.preventDefault();
  const orgId = document.getElementById('ws-create-org-id').value.trim();
  const body = {
    name: document.getElementById('ws-name').value,
    domain: document.getElementById('ws-domain').value,
    platform: document.getElementById('ws-platform').value
  };
  try {
    await apiFetch(`/organizations/${encodeURIComponent(orgId)}/websites`, { method: 'POST', body: JSON.stringify(body) });
    setStatus('ws-create-status', '<span style="color:var(--success)">&#10003; Created</span>');
    e.target.reset();
    document.getElementById('ws-org-id').value = orgId;
    listWebsites();
  } catch (err) {
    setStatus('ws-create-status', `<span style="color:var(--danger)">&#10007; ${err.status || 'Error'}</span>`);
    showAlert('ws-alert', `Create failed: ${JSON.stringify(err.data || err.message || err)}`);
  }
}

async function listOrders() {
  const orgId = document.getElementById('ord-org-id').value.trim();
  const query = orgId ? `?organizationId=${encodeURIComponent(orgId)}` : '';
  try {
    const data = await apiFetch(`/orders${query}`);
    const rows = Array.isArray(data) ? data : (data.content || data.data || []);
    document.getElementById('ord-table').innerHTML = renderTable(
      ['ID', 'External ID', 'Status', 'Amount', 'Currency', 'Created'],
      rows,
      r => `${idCell(r.id || r.orderId || '')}
            <td>${escHtml(r.externalOrderId || r.externalId || '')}</td>
            <td>${badgeHtml(r.status)}</td>
            <td>${escHtml(r.totalAmount != null ? r.totalAmount : '—')}</td>
            <td>${escHtml(r.currency || '')}</td>
            <td>${escHtml(r.createdAt ? r.createdAt.split('T')[0] : '—')}</td>`
    );
  } catch (e) {
    showAlert('ord-alert', `Failed to load orders: ${e.status || ''} ${JSON.stringify(e.data || e.message || e)}`);
  }
}

async function createOrder(e) {
  e.preventDefault();
  const body = {
    orgId: document.getElementById('ord-create-org-id').value.trim(),
    websiteId: document.getElementById('ord-website-id').value.trim(),
    externalOrderId: document.getElementById('ord-external-id').value,
    externalOrderNumber: document.getElementById('ord-external-number').value || undefined,
    status: document.getElementById('ord-status').value || 'CREATED',
    orderTotal: parseFloat(document.getElementById('ord-amount').value) || undefined,
    currency: (document.getElementById('ord-currency').value || 'USD').toUpperCase()
  };
  try {
    await apiFetch('/orders', { method: 'POST', body: JSON.stringify(body) });
    setStatus('ord-create-status', '<span style="color:var(--success)">&#10003; Created</span>');
    e.target.reset();
    document.getElementById('ord-currency').value = 'USD';
    listOrders();
  } catch (err) {
    setStatus('ord-create-status', `<span style="color:var(--danger)">&#10007; ${err.status || 'Error'}</span>`);
    showAlert('ord-alert', `Create failed: ${JSON.stringify(err.data || err.message || err)}`);
  }
}

async function listFulfillments() {
  const orderId = document.getElementById('ful-order-id').value.trim();
  if (!orderId) { showAlert('ful-alert', 'Please enter an Order ID'); return; }
  try {
    const data = await apiFetch(`/orders/${encodeURIComponent(orderId)}/fulfillments`);
    const rows = Array.isArray(data) ? data : (data.content || data.data || []);
    document.getElementById('ful-table').innerHTML = renderTable(
      ['ID', 'Carrier', 'Tracking #', 'Status', 'Created'],
      rows,
      r => `${idCell(r.id || r.fulfillmentId || '')}
            <td>${escHtml(r.carrier || '')}</td>
            <td>${escHtml(r.trackingNumber || '')}</td>
            <td>${badgeHtml(r.status)}</td>
            <td>${escHtml(r.createdAt ? r.createdAt.split('T')[0] : '—')}</td>`
    );
  } catch (e) {
    showAlert('ful-alert', `Failed to load fulfillments: ${e.status || ''} ${JSON.stringify(e.data || e.message || e)}`);
  }
}

async function createFulfillment(e) {
  e.preventDefault();
  const orderId = document.getElementById('ful-create-order-id').value.trim();
  const body = {
    externalFulfillmentId: document.getElementById('ful-external-id').value,
    carrier: document.getElementById('ful-carrier').value || undefined,
    serviceLevel: document.getElementById('ful-service-level').value || undefined,
    status: document.getElementById('ful-status').value || 'CREATED'
  };
  try {
    await apiFetch(`/orders/${encodeURIComponent(orderId)}/fulfillments`, { method: 'POST', body: JSON.stringify(body) });
    setStatus('ful-create-status', '<span style="color:var(--success)">&#10003; Created</span>');
    e.target.reset();
    document.getElementById('ful-order-id').value = orderId;
    listFulfillments();
  } catch (err) {
    setStatus('ful-create-status', `<span style="color:var(--danger)">&#10007; ${err.status || 'Error'}</span>`);
    showAlert('ful-alert', `Create failed: ${JSON.stringify(err.data || err.message || err)}`);
  }
}

async function listTracking() {
  const fulfillmentId = document.getElementById('trk-fulfillment-id').value.trim();
  if (!fulfillmentId) { showAlert('trk-alert', 'Please enter a Fulfillment ID'); return; }
  try {
    const data = await apiFetch(`/fulfillments/${encodeURIComponent(fulfillmentId)}/tracking`);
    const rows = Array.isArray(data) ? data : (data.content || data.data || []);
    document.getElementById('trk-table').innerHTML = renderTable(
      ['ID', 'Status', 'Location', 'Description', 'Timestamp'],
      rows,
      r => `${idCell(r.id || r.trackingEventId || '')}
            <td>${badgeHtml(r.status)}</td>
            <td>${escHtml(r.location || '—')}</td>
            <td>${escHtml(r.description || '—')}</td>
            <td>${escHtml(r.eventTime || r.timestamp || r.createdAt || '—')}</td>`
    );
  } catch (e) {
    showAlert('trk-alert', `Failed to load tracking: ${e.status || ''} ${JSON.stringify(e.data || e.message || e)}`);
  }
}

async function createTracking(e) {
  e.preventDefault();
  const fulfillmentId = document.getElementById('trk-create-fulfillment-id').value.trim();
  const body = {
    trackingNumber: document.getElementById('trk-tracking-number').value,
    carrier: document.getElementById('trk-carrier').value || undefined,
    trackingUrl: document.getElementById('trk-url').value || undefined,
    status: document.getElementById('trk-status').value || 'IN_TRANSIT',
    isPrimary: document.getElementById('trk-is-primary').checked
  };
  try {
    await apiFetch(`/fulfillments/${encodeURIComponent(fulfillmentId)}/tracking`, { method: 'POST', body: JSON.stringify(body) });
    setStatus('trk-create-status', '<span style="color:var(--success)">&#10003; Created</span>');
    e.target.reset();
    document.getElementById('trk-fulfillment-id').value = fulfillmentId;
    listTracking();
  } catch (err) {
    setStatus('trk-create-status', `<span style="color:var(--danger)">&#10007; ${err.status || 'Error'}</span>`);
    showAlert('trk-alert', `Create failed: ${JSON.stringify(err.data || err.message || err)}`);
  }
}

async function checkHealth() {
  const dot = document.getElementById('health-dot');
  const text = document.getElementById('health-text');
  try {
    const res = await fetch('/actuator/health');
    if (res.ok) {
      const data = await res.json().catch(() => ({}));
      dot.className = 'dot online';
      text.textContent = `API Online — ${data.status || 'UP'}`;
    } else {
      throw new Error(res.status);
    }
  } catch {
    try {
      await fetch('/organizations');
      dot.className = 'dot online';
      text.textContent = 'API Online';
    } catch {
      dot.className = 'dot offline';
      text.textContent = 'API Offline';
    }
  }
}

checkHealth();
setInterval(checkHealth, 30000);
