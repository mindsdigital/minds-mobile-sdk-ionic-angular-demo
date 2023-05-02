import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
    selector: '[phoneMask]'
})
export class PhoneMaskDirective {
    constructor(private el: ElementRef) { }

    @HostListener('input', ['$event'])
    onInputChange(event: any) {
        let value = event.target.value.replace(/\D/g, '');
        value = value.substring(0, 11);
        value = value.replace(/^(\d{2})(\d)/g, '($1) $2');
        value = value.replace(/(\d{4})(\d)/, '$1-$2');
        event.target.value = value;
    }
}
