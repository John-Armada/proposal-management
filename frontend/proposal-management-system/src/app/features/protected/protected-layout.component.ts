import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-protected-layout',
  imports: [RouterOutlet],
  templateUrl: './protected-layout.component.html',
})
export class ProtectedLayout { }
