export interface ContentType {
  id: string;
  name: string;
}

export interface Genre {
  id: string;
  name: string;
}

export interface Content {
  id: string;
  title: string;
  description: string;
  releaseDate: string;
  duration: string;
  language: string;
  thumbnailUrl: string;
  videoUrl: string;
  isAvailable: boolean;
  createdAt: string;
  updatedAt: string;
  contentType: ContentType;
  genres: Genre[];
  episodeCount: number;
  episodes?: Episode[];
}

export interface Episode {
  id: string;
  seasonNumber: number;
  episodeNumber: number;
  title: string;
  description: string;
  duration: string;
  releaseDate: string;
  thumbnailUrl: string;
  videoUrl: string;
  createdAt: string;
  updatedAt: string;
  contentId: string;
  contentTitle: string;
}

export interface Season {
  seasonNumber: number;
  episodeCount: number;
}

export interface EpisodePage {
  content: Episode[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ContentPage {
  content: Content[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}