package org.biodatageeks.utils

object BDGInternalParams {

  /*A source directory of a table involded in BAM CTAS operation*/
  final val BAMCTASDir = "spark.biodatageeks.bam.bam_ctas_dir"
  final val BAMCTASFilter = "spark.biodatageeks.bam.bam_ctas_filter"
  final val BAMCTASLimit = "spark.biodatageeks.bam.bam_ctas_limit"
  final val BAMCTASHeaderPath =  "spark.biodatageeks.bam.bam_header_path"
  final val BAMCTASOutputPath = "spark.biodatageeks.bam.output_path"
  final val BAMCTASCmd = "spark.biodatageeks.bam.bam_ctas_cmd"

  /*referenced column*/
  final val SAMPLE_COLUMN_NAME = "sampleId"

  /* parameter determining whether all positions in contig are included in output */
  final val CoverageShowAllPositions = "spark.biodatageeks.coverage.allPositions"

  final val CoverageResultTypeBlocks="blocks"
  final val CoverageResultTypeBases="bases"
}