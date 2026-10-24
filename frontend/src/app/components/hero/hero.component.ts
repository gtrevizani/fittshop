import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css']
})
export class HeroComponent {
  @Output() selectGoalEvent = new EventEmitter<string>();
  @Output() openRecommenderEvent = new EventEmitter<void>();

  selectedGoal = 'TODOS';

  goals = [
    { id: 'HIPERTROFIA', label: 'Hipertrofia', icon: '💪', desc: 'Ganho de Massa & Força' },
    { id: 'DEFINICAO', label: 'Definição', icon: '🔥', desc: 'Queima & Recomposição' },
    { id: 'FORCA_POWERLIFTING', label: 'Powerlifting', icon: '⚡', desc: 'Carga Máxima & Explosão' },
    { id: 'RESISTENCIA_CARDIO', label: 'Resistência', icon: '🏃', desc: 'Endurance & Recuperação' }
  ];

  selectGoal(goalId: string): void {
    this.selectedGoal = this.selectedGoal === goalId ? 'TODOS' : goalId;
    this.selectGoalEvent.emit(this.selectedGoal);
  }
}
