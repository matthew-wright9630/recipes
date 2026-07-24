import { Injectable } from '@angular/core';
import { ReplaySubject, Subject } from 'rxjs';
import { Cookbook } from '../../models/cookbook';

@Injectable({
  providedIn: 'root',
})
export class CookbookStateServiceService {
  private cookbookUpdated = new ReplaySubject<Cookbook>(1);
  cookbookUpdated$ = this.cookbookUpdated.asObservable();

  private cookbookDeleted = new Subject<number>();
  cookbookDeleted$ = this.cookbookDeleted.asObservable();

  notifycookbookUpdated(cookbook: Cookbook): void {
    this.cookbookUpdated.next(cookbook);
  }

  notifycookbookDeleted(id: number): void {
    this.cookbookDeleted.next(id);
  }
}
