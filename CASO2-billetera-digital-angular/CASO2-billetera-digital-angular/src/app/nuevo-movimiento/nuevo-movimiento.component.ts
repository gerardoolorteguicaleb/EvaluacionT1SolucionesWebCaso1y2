import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface SolicitudMovimiento {
  tipo: 'Recarga' | 'Gasto';
  monto: number;
}

@Component({
  selector: 'app-nuevo-movimiento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card border-0 shadow-sm">
      <div class="card-body">
        <h5 class="mb-3">Nuevo movimiento</h5>

        <form (ngSubmit)="registrar()" class="row g-3" novalidate>

          <div class="col-md-4">
            <label class="form-label" for="tipoMovimiento">Tipo de movimiento</label>
            <select
              id="tipoMovimiento"
              class="form-select"
              name="tipo"
              [(ngModel)]="tipo">
              <option value="Recarga">Recarga</option>
              <option value="Gasto">Gasto</option>
            </select>
          </div>

          <div class="col-md-4">
            <label class="form-label" for="montoMovimiento">Monto (S/)</label>
            <input
              id="montoMovimiento"
              class="form-control"
              type="number"
              name="monto"
              [(ngModel)]="monto"
              min="0.01"
              step="0.01"
              placeholder="0.00"
            >
          </div>

          @if (mensajeError) {
            <div class="col-12">
              <div class="alert alert-danger py-2 mb-0">
                {{ mensajeError }}
              </div>
            </div>
          }

          <div class="col-md-4 d-flex align-items-end">
            <button
              class="btn btn-primary w-100"
              type="submit"
              [disabled]="!esValido">
              Registrar movimiento
            </button>
          </div>

        </form>
      </div>
    </div>
  `
})
export class NuevoMovimientoComponent {

  @Input() saldoActual = 250;

  @Output() movimientoValido =
    new EventEmitter<SolicitudMovimiento>();

  tipo: 'Recarga' | 'Gasto' = 'Gasto';
  monto: number | null = null;

  // Mensaje calculado automáticamente
  get mensajeError(): string {
    if (this.monto === null || this.monto === undefined) {
      return '';
    }

    const m = Number(this.monto);

    if (!Number.isFinite(m) || m <= 0) {
      return 'Ingrese un monto numérico mayor que cero.';
    }

    if (this.tipo === 'Gasto' && m > this.saldoActual) {
      return 'Saldo insuficiente para realizar el gasto.';
    }

    return '';
  }

  // Habilitar o deshabilitar botón
  get esValido(): boolean {
    return this.mensajeError === '' &&
      this.monto !== null &&
      Number(this.monto) > 0;
  }

  registrar(): void {
    if (!this.esValido) return;

    const m = Number(this.monto);

    this.movimientoValido.emit({
      tipo: this.tipo,
      monto: Math.round(m * 100) / 100
    });

    this.monto = null;
  }
}
