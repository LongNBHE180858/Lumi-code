export type { ApiResponse } from 'app/shared/model/api-response.model';

export interface AccountProfile {
  id: number;
  login: string | null;
  firstName: string | null;
  lastName: string | null;
  fullName: string | null;
  email: string | null;
  imageUrl: string | null;
  authorities: string[] | null;

  bio: string | null;
  phoneNumber: string | null;
  schoolName: string | null;
  gradeLevel: number | null;
  verificationStatus: string | null;
}

export type ProfileUpdate = Pick<
  AccountProfile,
  'firstName' | 'lastName' | 'imageUrl' | 'phoneNumber' | 'schoolName' | 'gradeLevel' | 'bio'
>;

export interface ProfileStatistics {
  enrolledCourseCount: number;
  currentStreak: number;
  longestStreak: number;
  gemsBalance: number;
}

export interface LinkedStudent {
  linkId: number;
  studentId: number;
  fullName: string | null;
  status: string | null;
}
