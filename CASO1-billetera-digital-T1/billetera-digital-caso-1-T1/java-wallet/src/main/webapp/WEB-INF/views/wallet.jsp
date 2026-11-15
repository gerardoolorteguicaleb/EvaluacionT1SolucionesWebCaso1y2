<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="Billetera digital Java Servlet y JSP con recargas e historial de movimientos.">
  <title>Caso 1 · Billetera Digital: Recarga y Movimientos</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    :root { color-scheme: light; --wallet-forest:#194d3d; --wallet-forest-dark:#123a2f; --wallet-surface:#ffffff; --wallet-paper:#f5f7f3; --wallet-ink:#19241e; --wallet-muted:#68736b; --wallet-line:#e3e8e1; --wallet-income:#3b7350; --wallet-expense:#bd6048; --wallet-warm:#fff4dd; --wallet-sky:#e9f2ed; }
    body { min-height:100vh; background:var(--wallet-paper); color:var(--wallet-ink); font-family:system-ui,-apple-system,"Segoe UI",sans-serif; }
    .topbar { border-bottom:1px solid var(--wallet-line); background:var(--wallet-surface); }
    .wallet-mark { display:grid; width:38px; height:38px; place-items:center; border-radius:12px; background:var(--wallet-forest); color:white; font-size:19px; }
    .main { max-width:1040px; }
    .eyebrow { color:var(--wallet-muted); font-size:11px; font-weight:700; letter-spacing:.08em; }
    .summary { background:var(--wallet-forest); color:white; border-radius:12px; }
    .summary-balance { font-size:clamp(38px,7vw,52px); font-weight:600; letter-spacing:-.04em; font-variant-numeric:tabular-nums; }
    .metric,.panel { border:1px solid var(--wallet-line); border-radius:11px; background:var(--wallet-surface); }
    .movement { border-top:1px solid var(--wallet-line); }
    .positive { color:var(--wallet-income); }
    .negative { color:var(--wallet-expense); }
    .example-note { background:var(--wallet-warm); border-radius:8px; }
    .btn-success { --bs-btn-bg:var(--wallet-forest); --bs-btn-border-color:var(--wallet-forest); --bs-btn-hover-bg:var(--wallet-forest-dark); --bs-btn-hover-border-color:var(--wallet-forest-dark); }
    .form-control:focus,.form-select:focus { border-color:var(--wallet-forest); box-shadow:0 0 0 .2rem rgb(25 77 61 / 15%); }
    @media(max-width:575px) { .content-gutter { padding-inline:14px!important; } .movement-amount { font-size:12px; } }
  </style>
</head>
<body>
  <fmt:setLocale value="es_PE" />
  <header class="topbar">
    <div class="container d-flex align-items-center justify-content-between py-3 px-3 px-sm-4">
      <a href="${pageContext.request.contextPath}/wallet" class="d-flex align-items-center gap-2 text-dark text-decoration-none fw-semibold">
        <span class="wallet-mark" aria-hidden="true">▤</span>
        <span>Billetera Digital</span>
      </a>
      <span class="badge text-bg-success rounded-pill">Caso 1 · Java</span>
    </div>
  </header>

  <main class="container main content-gutter px-3 px-sm-4 py-4 py-md-5">
    <div class="mb-4">
      <p class="eyebrow mb-2">SOLUCIONES WEB · EVALUACIÓN T1</p>
      <h1 class="h2 fw-semibold mb-2">Billetera Digital: Recarga y Movimientos</h1>
      <p class="text-secondary mb-0">Controla tu saldo, registra movimientos y consulta tu historial.</p>
    </div>

    <c:if test="${not empty successMessage}">
      <div class="alert alert-success d-flex gap-2 align-items-center" role="status"><span aria-hidden="true">✓</span><c:out value="${successMessage}" /></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
      <div class="alert alert-danger d-flex gap-2 align-items-start" role="alert"><span aria-hidden="true">!</span><span><c:out value="${errorMessage}" /></span></div>
    </c:if>

    <section class="row g-3 mb-4" aria-label="Saldo y movimientos">
      <div class="col-12 col-md-7">
        <div class="summary h-100 p-4 p-md-5 d-flex flex-column justify-content-between">
          <span class="small opacity-75">SALDO DISPONIBLE · SOLES PERUANOS</span>
          <p class="summary-balance my-4">S/ <fmt:formatNumber value="${wallet.balance}" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true" /></p>
          <span class="small opacity-75">Tu saldo y movimientos permanecen en esta sesión.</span>
        </div>
      </div>
      <div class="col-12 col-md-5">
        <div class="metric h-100 p-4 d-flex flex-column justify-content-center gap-2">
          <span class="small text-secondary">Movimientos registrados</span>
          <span class="h2 fw-semibold mb-0"><c:out value="${wallet.movements.size()}" /></span>
          <span class="small text-secondary">Los más recientes aparecen primero.</span>
        </div>
      </div>
    </section>

    <section class="row g-3 align-items-start">
      <div class="col-12 col-lg-5">
        <section class="panel p-4" aria-labelledby="form-heading">
          <h2 class="h5 fw-semibold" id="form-heading">Nuevo movimiento</h2>
          <p class="small text-secondary mb-4">Registra una recarga o un gasto.</p>
          <form action="${pageContext.request.contextPath}/wallet" method="post" novalidate>
            <div class="mb-3">
              <label class="form-label small fw-semibold" for="type">Tipo de movimiento</label>
              <select class="form-select" name="type" id="type" required>
                <option value="RECARGA" ${submittedType == 'RECARGA' ? 'selected' : ''}>Recarga</option>
                <option value="GASTO" ${submittedType == 'GASTO' ? 'selected' : ''}>Gasto</option>
              </select>
            </div>
            <div class="mb-4">
              <label class="form-label small fw-semibold" for="amount">Monto</label>
              <div class="input-group">
                <span class="input-group-text">S/</span>
                <input class="form-control" type="number" name="amount" id="amount" min="0.01" step="0.01" inputmode="decimal" placeholder="0.00" value="<c:out value='${submittedAmount}' />" aria-describedby="amount-help">
              </div>
              <div class="form-text" id="amount-help">Ingresa un monto positivo con hasta dos decimales.</div>
            </div>
            <button class="btn btn-success w-100" type="submit">Registrar movimiento</button>
          </form>
        </section>
      </div>

      <div class="col-12 col-lg-7">
        <section class="panel p-4" aria-labelledby="history-heading">
          <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
            <div><h2 class="h5 fw-semibold mb-1" id="history-heading">Movimientos recientes</h2><p class="small text-secondary mb-0">Historial de esta sesión.</p></div>
            <span class="badge rounded-pill text-bg-light border"><c:out value="${wallet.movements.size()}" /></span>
          </div>
          <c:choose>
            <c:when test="${empty wallet.movements}">
              <div class="text-center py-5" data-testid="empty-history">
                <div class="h3 text-secondary" aria-hidden="true">▤</div>
                <p class="fw-semibold mb-1">Aún no hay movimientos</p>
                <p class="small text-secondary mb-0">Cuando registres una recarga o un gasto, aparecerá aquí.</p>
              </div>
            </c:when>
            <c:otherwise>
              <div class="d-flex py-2 small text-secondary justify-content-between"><span>TIPO</span><span>MONTO · ORDEN</span></div>
              <c:forEach items="${wallet.movements}" var="movement" varStatus="status">
                <div class="movement d-flex justify-content-between align-items-center gap-3 py-3">
                  <div class="d-flex align-items-center gap-3">
                    <span aria-hidden="true">${movement.type == 'RECARGA' ? '↙' : '↗'}</span>
                    <span><strong class="d-block"><c:out value="${movement.label}" /></strong><small class="text-secondary"><c:out value="${movement.formattedDate}" /></small></span>
                  </div>
                  <span class="movement-amount text-end ${movement.type == 'RECARGA' ? 'positive' : 'negative'}">${movement.type == 'RECARGA' ? '+' : '−'}S/ <fmt:formatNumber value="${movement.amount}" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true" /> <small class="text-secondary">· #${status.count}</small></span>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </section>
      </div>
    </section>

    <section class="example-note p-3 mt-3" aria-labelledby="validation-heading">
      <h2 class="small fw-semibold mb-2" id="validation-heading">Validación de ejemplo</h2>
      <p class="small mb-0">Montos vacíos, inválidos o menores o iguales a cero se rechazan. Un gasto mayor que el saldo muestra un error comprensible; no altera el saldo ni el historial.</p>
    </section>

    <footer class="small text-secondary d-flex justify-content-between flex-wrap gap-2 pt-4">
      <span>Java 17 · Servlet 6 · JSP y JSTL · Bootstrap 5.3</span>
      <a href="${pageContext.request.contextPath}/wallet" class="link-success">Billetera Digital · T1</a>
    </footer>
  </main>
</body>
</html>
