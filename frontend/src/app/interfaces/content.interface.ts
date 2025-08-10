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