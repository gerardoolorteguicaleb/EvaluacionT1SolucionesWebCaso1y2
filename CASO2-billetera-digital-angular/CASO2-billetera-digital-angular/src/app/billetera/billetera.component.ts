import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NuevoMovimientoComponent, SolicitudMovimiento } from '../nuevo-movimiento/nuevo-movimiento.component';

interface Movimiento extends SolicitudMovimiento { fecha: Date; }


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    NuevoMovimientoComponent
  ],
  template: `

    <!-- Barra superior -->
    <nav class="navbar navbar-dark" style="background: #17263c;">
      <div class="container">
        <span class="navbar-brand fw-semibold">
          Mi Billetera Digital
        </span>

        <span class="badge text-bg-secondary">
          Angular
        </span>
      </div>
    </nav>

    <main class="container py-4">

      <!-- Saldo y estado de la billetera -->
      <div class="row g-3 mb-4">

        <!-- Saldo disponible -->
        <div class="col-md-5">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">
              <small class="text-secondary">
                SALDO DISPONIBLE
              </small>

              <h2 class="text-success fw-bold mt-2">
                S/ {{ saldo | number:'1.2-2' }}
              </h2>

              <small class="text-secondary">
                Saldo actualizado de tu billetera
              </small>
            </div>
          </div>
        </div>

        <!-- Información del componente padre -->
        <div class="col-md-7">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body">

              <span class="badge text-bg-primary mb-2">
                Componente padre
              </span>

              <h5>Estado de la billetera</h5>

              <p class="text-secondary mb-0">
                El componente padre administra el saldo y el historial.
                El componente hijo comunica solicitudes válidas
                mediante &#64;Output().
              </p>

            </div>
          </div>
        </div>

      </div>

      <!-- Historial de movimientos -->
      <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">

          <h5 class="mb-3">
            Movimientos recientes
          </h5>

          <!-- Mensaje y tabla controlados con el nuevo flujo de control -->
          @if (movimientos.length === 0) {
            <div class="alert alert-light mb-0">
              Aún no hay movimientos.
            </div>
          } @else {
            <div class="table-responsive">
              <table class="table align-middle mb-0">

                <thead>
                  <tr>
                    <th>Tipo</th>
                    <th>Monto (S/)</th>
                    <th>Fecha</th>
                  </tr>
                </thead>

                <tbody>
                  @for (m of movimientos; track $index) {
                    <tr>

                      <!-- Tipo de movimiento -->
                      <td>
                        <span
                          class="badge"
                          [class.text-bg-success]="m.tipo === 'Recarga'"
                          [class.text-bg-danger]="m.tipo === 'Gasto'"
                        >
                          {{ m.tipo }}
                        </span>
                      </td>

                      <!-- Importe -->
                      <td
                        class="fw-semibold"
                        [class.text-success]="m.tipo === 'Recarga'"
                        [class.text-danger]="m.tipo === 'Gasto'"
                      >
                        {{ m.tipo === 'Recarga' ? '+' : '−' }}
                        S/ {{ m.monto | number:'1.2-2' }}
                      </td>

                      <!-- Fecha del movimiento -->
                      <td class="text-secondary">
                        {{ m.fecha | date:'dd/MM/yyyy HH:mm' }}
                      </td>

                    </tr>
                  }
                </tbody>

              </table>
            </div>
          }

        </div>
      </div>

      <!-- Componente hijo: formulario de movimientos -->
      <app-nuevo-movimiento
        [saldoActual]="saldo"
        (movimientoValido)="procesarMovimiento($event)"
      ></app-nuevo-movimiento>

    </main>
  `
})

export class BilleteraComponent {
  saldo = 250.00;
  movimientos: Movimiento[] = [];
  procesarMovimiento(solicitud: SolicitudMovimiento): void {
    if (!Number.isFinite(solicitud.monto) || solicitud.monto <= 0) return;
    if (solicitud.tipo === 'Gasto' && solicitud.monto > this.saldo) return;
    this.saldo = Math.round((this.saldo + (solicitud.tipo === 'Recarga' ? solicitud.monto : -solicitud.monto)) * 100) / 100;
    this.movimientos = [{ ...solicitud, fecha: new Date() }, ...this.movimientos];
  }
}
