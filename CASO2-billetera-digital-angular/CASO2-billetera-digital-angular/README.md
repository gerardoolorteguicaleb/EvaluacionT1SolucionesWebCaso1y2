# Caso 2 – Billetera Digital (Angular)

## Requisitos
- Node.js 18.13 o superior (recomendado Node 20 LTS)
- npm

## Instalación y ejecución
1. Abrir una terminal en esta carpeta.
2. Ejecutar `npm install`.
3. Ejecutar `npm start`.
4. Abrir `http://localhost:4200` si el navegador no se abre automáticamente.

## Componentes propios
- `BilleteraComponent`: componente padre; es el único responsable de modificar saldo e historial.
- `NuevoMovimientoComponent`: componente hijo; recibe saldo con `@Input()` y emite solicitudes válidas con `@Output()`.

No se usa backend, base de datos ni API. El estado se mantiene en memoria mientras la aplicación está abierta.
