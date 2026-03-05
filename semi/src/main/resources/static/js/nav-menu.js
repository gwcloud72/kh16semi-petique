// nav-menu.js - 상단 내비게이션 메뉴 UI 스크립트
(function () {
	function closeMenu(menu) {
		if (!menu) return;
		menu.classList.remove('is-open');
		var btn = menu.querySelector('.nav-menu__btn');
		if (btn) btn.setAttribute('aria-expanded', 'false');
	}

	function toggleMenu(menu) {
		if (!menu) return;
		var open = menu.classList.toggle('is-open');
		var btn = menu.querySelector('.nav-menu__btn');
		if (btn) btn.setAttribute('aria-expanded', open ? 'true' : 'false');
	}

	document.addEventListener('DOMContentLoaded', function () {
		var menu = document.querySelector('.nav-menu');
		if (!menu) return;

		var btn = menu.querySelector('.nav-menu__btn');
		var panel = menu.querySelector('.nav-menu__panel');

		if (btn) {
			btn.addEventListener('click', function (e) {
				e.preventDefault();
				toggleMenu(menu);
			});
		}

		document.addEventListener('click', function (e) {
			if (!menu.classList.contains('is-open')) return;
			if (menu.contains(e.target)) return;
			closeMenu(menu);
		});

		document.addEventListener('keydown', function (e) {
			if (e.key !== 'Escape') return;
			if (!menu.classList.contains('is-open')) return;
			closeMenu(menu);
		});

		if (panel) {
			panel.addEventListener('click', function (e) {
				var a = e.target.closest('a');
				if (a) closeMenu(menu);
			});
		}
	});
})();
