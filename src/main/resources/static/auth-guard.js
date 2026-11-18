// セッション切れ（401）のときはログイン画面へ戻す
(function () {
  const orig = window.fetch;
  window.fetch = async function (...args) {
    const res = await orig.apply(this, args);
    if (res.status === 401) {
      localStorage.removeItem('loginUser');
      location.href = '/login.html';
    }
    return res;
  };
})();
