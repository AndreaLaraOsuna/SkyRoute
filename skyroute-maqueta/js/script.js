// ---------- modales ----------

function abrirModal(id) {
    document.getElementById(id).classList.add('visible');
}

function cerrarModal(id) {
    document.getElementById(id).classList.remove('visible');
}

// la X y los botones "Cancelar" de los modales
document.querySelectorAll('[data-cerrar-modal]').forEach(function (boton) {
    boton.addEventListener('click', function () {
        cerrarModal(boton.closest('.modal-fondo').id);
    });
});

document.querySelectorAll('[data-abrir-modal]').forEach(function (boton) {
    boton.addEventListener('click', function () {
        abrirModal(boton.dataset.abrirModal);
    });
});

// si dan clic afuera de la ventanita también se cierra
document.querySelectorAll('.modal-fondo').forEach(function (fondo) {
    fondo.addEventListener('click', function (evento) {
        if (evento.target === fondo) {
            cerrarModal(fondo.id);
        }
    });
});
