import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';

import { routes } from './app.routes';
import {
  provideClientHydration,
  withEventReplay,
} from '@angular/platform-browser';
import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';
import { authInterceptor } from './shared/auth-interceptor';
import { AuthStateService } from './shared/services/auth-state-service/auth-state.service';
import { firstValueFrom } from 'rxjs';
import { loadingInterceptor } from './loading-interceptor';
import { errorInterceptor } from './shared/error-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
      }),
    ),
    // provideClientHydration(withEventReplay()),
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor, loadingInterceptor, errorInterceptor]),
    ),
    provideAppInitializer(() => {
      const authState = inject(AuthStateService);
      return firstValueFrom(authState.initialize());
    }),
    // provideHttpClient(withInterceptors([loadingInterceptor])),
  ],
};
