export interface TeachingScope {
  subjectId: number | null;
  subjectName: string | null;
  gradeId: number | null;
  gradeLevel: number | null;
}

export interface InstructorPublicProfile {
  id: number;
  fullName: string | null;
  imageUrl: string | null;
  bio: string | null;
  teachingScopes: TeachingScope[] | null;
}

export interface InstructorPublicCourse extends TeachingScope {
  id: number;
  title: string;
  description: string | null;
  price: number | null;
  validDurationDays: number | null;
}
