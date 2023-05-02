import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
    selector: '[cpfMask]'
})
export class CpfMaskDirective {
    constructor(private el: ElementRef) { }

    @HostListener('input', ['$event'])
    onInputChange(event: any) {
        let value = event.target.value.replace(/\D/g, '');
        value = value.substring(0, 11);
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        event.target.value = value;
    }
}