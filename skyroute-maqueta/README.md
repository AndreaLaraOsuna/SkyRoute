# SkyRoute – Sistema de Reservas de Aerolínea

Proyecto final: frontend responsive (Mobile First) de un sistema de reservas de vuelos con dos áreas: **Usuario** y **Administrador**.

Construido únicamente con **HTML5, CSS3 y JavaScript puro**. No usa frameworks, plantillas ni backend.

Casi todo funciona solo con HTML y CSS: menús, selección de asientos, pestañas, vistas de Clientes y Bloqueos, filtro de días de la Agenda y validación de formularios. JavaScript se usa únicamente para los modales, los buscadores, el botón de ver contraseña y para decidir a qué área entra cada cuenta en el login.

## Cómo ejecutarlo

1. Descarga o clona el repositorio.
2. Abre `index.html` en el navegador (doble clic) o usa la extensión **Live Server** de VS Code.
3. Inicia sesión con alguna de estas cuentas simuladas:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@skyroute.com` | `admin123` |
| Usuario | cualquier correo válido | mínimo 6 caracteres |

## Estructura

```text
skyroute/
├── index.html                      Login
├── README.md
├── css/
│   ├── global.css                  Variables, reset y estilos base
│   ├── componentes.css             Componentes reutilizables
│   └── estilos.css                 Layouts, páginas y media queries
├── js/
│   └── script.js                   Modales, buscadores y login
├── imgs/
│   ├── iconos/                     Iconos SVG (incluidos)
│   ├── nubes.svg                   Nubes del login (incluida)
│   ├── hero-cielo.jpg              PENDIENTE
│   ├── logo-aeromexico.png         PENDIENTE
│   └── logo-volaris.png            PENDIENTE
└── paginas/
    ├── usuario/
    │   ├── registro.html
    │   ├── inicio.html
    │   ├── reservar.html
    │   ├── mis-reservas.html
    │   └── contacto.html
    └── administrador/
        ├── reservaciones.html
        ├── nueva-reservacion.html
        ├── realizar-reserva.html
        ├── detalle-reservacion.html
        ├── clientes.html
        ├── agenda.html
        ├── resumen.html
        ├── nueva-ruta.html
        ├── horarios.html
        └── bloqueos.html
```

## Imágenes pendientes

Coloca estos archivos en `imgs/` con exactamente estos nombres:

| Archivo | Dónde se usa |
|---|---|
| `hero-cielo.jpg` | Fondo del hero en Inicio, Mis Reservas, Datos de reserva y Contacto (foto del ala del avión sobre las nubes) |
| `logo-aeromexico.png` | Tarjetas de vuelos y reservas de Aeroméxico |
| `logo-volaris.png` | Tarjetas de vuelos y reservas de Volaris |

Mientras no existan, el hero se muestra en azul marino y los logos muestran su texto alternativo.

## Identidad visual

- Tipografía: **Montserrat** (400, 500, 600 y 700).
- Colores: `#102A56`, `#1557A6`, `#F8FAFC`, `#6C6969`, `#081A36`, `#A1EB9F`, `#DED555` y `#FF8A84`.
- Estados: Confirmada/Completada en verde, Pendiente en amarillo y Cancelada en coral.

## Puntos de quiebre

- Mobile: estilos base.
- Tablet: a partir de `768px`.
- Desktop: a partir de `1024px`.
