import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Cookbook } from './cookbook';

describe('Cookbook', () => {
  let component: Cookbook;
  let fixture: ComponentFixture<Cookbook>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cookbook],
    }).compileComponents();

    fixture = TestBed.createComponent(Cookbook);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
