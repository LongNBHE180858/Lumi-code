import { describe, expect, it } from 'vitest';
import { objectivesFromText, objectivesToText, optionalNumber } from './course-builder-fields';

describe('course builder form values', () => {
  it('preserves unset numeric values without silently setting free pricing or zero duration', () => {
    for (const value of ['', '  ', null, undefined]) expect(optionalNumber(value)).toBeNull();
    expect(optionalNumber('0')).toBe(0);
    expect(optionalNumber('12')).toBe(12);
  });
  it('preserves objective order and Vietnamese text through the editor', () => {
    const objectives = ['Đọc tài liệu PDF', 'Làm bài tập'];
    expect(objectivesFromText(objectivesToText(objectives))).toEqual(objectives);
    expect(objectivesFromText(' Đọc tài liệu PDF \r\n\r\n Làm bài tập ')).toEqual(objectives);
  });
  it('allows clearing objectives and accepts array values', () => {
    expect(objectivesFromText('')).toEqual([]);
    expect(objectivesFromText([' First ', '', 'Second'])).toEqual(['First', 'Second']);
    expect(objectivesFromText(null)).toBeNull();
    expect(objectivesToText(undefined)).toBe('');
  });
});
